package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.MyCardTreeDataBase;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardScrollListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.Container;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CardTreeViewModel extends AndroidViewModel {
    private final CardRepository mCardRepository;
    private final MutableLiveData<List<List<CardDTO>>> mListLiveDataByContainer;
    private List<List<CardDTO>> mAllData;

    private final List<Container> mPresentContainerList;
    private final List<List<Pair<CardDTO, CardState>>> mPresentData;

    private final View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    private View.OnDragListener onDragListenerForCardRecyclerView;
    private View.OnDragListener onDragListenerForEmptyCardSpace;

    private final int CARD_LOCATION_LEFT = 0;
    private final int CARD_LOCATION_RIGHT = 1;

    /* Default constructor*/
    public CardTreeViewModel(Application application) {
        super(application);
        Logger.message("VM#constructor");
        this.mCardRepository = new CardRepository(application);
        this.mListLiveDataByContainer = new MutableLiveData<>();
        this.mAllData = new ArrayList<>();
        this.mPresentData = new ArrayList<>();
        this.mPresentContainerList = new ArrayList<>();
//        mFocusCardPositions = new ArrayList<>();
//        mPresentFlags = new ArrayList<>();
//        mCardRvLayoutSuppressStates = new ArrayList<>();
        this.onLongListenerForCreateCardUtilFab = (view) -> view.startDragAndDrop(ClipData.newPlainText("", ""), new CardShadow(view), null, 0);
        initCardRecyclerViewDragListener();
        initEmptyCardSpaceDragListener();
    }

    /* remove card*/

    public void onRemoveBtnClicked(View view, CardDTO cardDTO) {
        alertDeleteWarning(view.getContext(), cardDTO, 0);
    }

    private void alertDeleteWarning(Context context, CardDTO headCardDTO, int followerCount) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        String headTitle = headCardDTO.getTitle();
        if (TextUtils.equals(headTitle, "")) {
            headTitle = "이름 미지정";
        }

        alertBuilder.setTitle("-카드 제거-")
                .setMessage(" 선택된 <" + headTitle + "> 카드와 함께,\n관련된 하위 '" + followerCount + "'개 카드를 지우시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("제거", new DialogInterface.OnClickListener() {
                    final List<Runnable> removeEventList = new ArrayList<>();

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<CardDTO> toRemoveList = new ArrayList<>();
                        toRemoveList.add(headCardDTO);
                        toRemoveList.addAll(findFollowers(headCardDTO));
                        mCardRepository.deletes(
                                dtoListToEntityList(toRemoveList)
                                , (deleteCount) -> {
                                    //count
                                    if (deleteCount > 0)
                                        return;
                                    applyRemoveAtAllList(toRemoveList, headCardDTO.getContainerNo());
                                    mListLiveDataByContainer.postValue(mAllData);
                                    applyRemoveAtPresentList(toRemoveList, headCardDTO.getContainerNo());

                                    // remove mutable/allData/presentData & notify.
                                }
                        );

                        //TODO : handle finish.
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "요청이 취소됐습니다.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void applyRemoveAtAllList(List<CardDTO> toRemoveList, int start) {
        Logger.message("vm#applyRemoveAtAllList");
        final int containerSize = mAllData.size();
        for (int i = start; i < containerSize; i++) {
            List<Pair<CardDTO, CardState>> containerData = mPresentData.get(i);
            for (Pair<CardDTO, CardState> pair : containerData) {
                CardDTO foundDTO = pair.first;
                boolean isContain = toRemoveList.contains(foundDTO);
                if (isContain) {
//                    containerData.remove(pair);
                }
            }

        }
    }

    private void applyRemoveAtPresentList(List<CardDTO> toRemoveList, int start) {
        Logger.message("vm#applyRemoveAtPresentList");
        final int containerSize = mPresentData.size();
        for (int i = start; i < containerSize; i++) {
            List<Pair<CardDTO, CardState>> containerData = mPresentData.get(i);
            for (Pair<CardDTO, CardState> pair : containerData) {
                CardDTO foundDTO = pair.first;
                boolean isContain = toRemoveList.contains(foundDTO);
                if (isContain) {
//                    containerData.remove(pair);
                }
            }

        }
    }

    private List<CardDTO> findFollowers(CardDTO headCardDTO) {
        Logger.message("vm#findFollowers");
        List<CardDTO> result = new ArrayList<>();
        return result;
    }

    /* Mode change*/
    public void onModeChanged(View view, boolean isOn) {
        Logger.message("vm#onModeChange");
        int newVisibility = View.INVISIBLE;
        if (isOn)
            newVisibility = View.VISIBLE;
        for (List<Pair<CardDTO, CardState>> containerItems : mPresentData) {
            for (Pair<CardDTO, CardState> item : containerItems) {
                item.second.setRemoveBtnVisibility(newVisibility);
            }
        }
        Toast.makeText(view.getContext(), "" + isOn, Toast.LENGTH_SHORT).show();
    }

    /* Drag and drop for add new card*/

    public void initEmptyCardSpaceDragListener() {
        Logger.message("vm#initEmptyCardSpaceDragListener");
        onDragListenerForEmptyCardSpace = (view, event) -> {
            if (!(view instanceof TextView)) {
                return false;
            }
            if (event.getAction() == DragEvent.ACTION_DRAG_STARTED)
                return true;
            if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
                Animation tremble = AnimationUtils.loadAnimation(view.getContext(), R.anim.card_trembling);
                view.startAnimation(tremble);
                return true;
            }
            if (event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
                view.clearAnimation();
                return true;
            }
            if (event.getAction() == DragEvent.ACTION_DROP) {
                view.clearAnimation();
                ConstraintLayout parent = (ConstraintLayout) view.getParent();
                RecyclerView containerRecyclerView = (RecyclerView) parent.getParent();
                int targetContainerPosition = containerRecyclerView.getChildAdapterPosition(parent);
                int rootCardNo = CardDTO.NO_ROOT_CARD;
                if (targetContainerPosition != 0) {
                    int rootCardSeqNo = mPresentContainerList.get(targetContainerPosition - 1).getFocusCardPosition();
                    rootCardNo = mPresentData.get(targetContainerPosition - 1).get(rootCardSeqNo).first.getCardNo();
                }
                dropAndCreateService(containerRecyclerView, rootCardNo, targetContainerPosition);
                return true;
            }
            return false;
        };
    }

    private void initCardRecyclerViewDragListener() {
        Logger.message("vm#initCardRecyclerViewDragListener");
        onDragListenerForCardRecyclerView = (view, event) -> {
            if (view instanceof RecyclerView) {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    return true;
                }
                RecyclerView targetView = (RecyclerView) view;
                LinearLayoutManager layoutManager = (LinearLayoutManager) targetView.getLayoutManager();
                NullPassUtil.checkLinearLayoutManager(layoutManager);
                if (!(Objects.requireNonNull(layoutManager).getItemCount() > 0)) {
                    return false;
                }
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                boolean result;
                if (firstVisibleItemPosition == lastVisibleItemPosition) {
                    result = handleDragEventSingleItemVisibleCase(targetView, firstVisibleItemPosition, event);
                    return result;
                }
                result = handleDragEventMultiItemVisibleCase(targetView, firstVisibleItemPosition, lastVisibleItemPosition, event);
                return result;
            } else {
                throw new RuntimeException("#ondrag() : recyclerview not found");
            }
        };

    }

    private boolean handleDragEventSingleItemVisibleCase(RecyclerView rv, int targetCardPosition, DragEvent event) {
        Logger.message("vm#handleDragEventSingleItemVisibleCase");
        FrameLayout targetView = Objects.requireNonNull((ContactCardViewHolder) rv.findViewHolderForAdapterPosition(targetCardPosition))
                .getBinding().cardContainerFrameLayout;
        NullPassUtil.checkFrameLayout(targetView);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                slowOut(targetView, false, CARD_LOCATION_LEFT);

                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                slowOut(targetView, true, CARD_LOCATION_LEFT);

                return true;
            case DragEvent.ACTION_DROP:
                dropAndCreateService(rv, targetView, null);
                return true;
        }
        return false;
    }

    private boolean handleDragEventMultiItemVisibleCase(RecyclerView rv, int firstVisibleCardPosition, int lastVisibleCardPosition, DragEvent event) {
        Logger.message("vm#handleDragEventMultiItemVisibleCase");
        ContactCardViewHolder firstVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(firstVisibleCardPosition);
        FrameLayout firstVisibleView = Objects.requireNonNull(firstVisibleViewVH).getBinding().cardContainerFrameLayout;
        ContactCardViewHolder lastVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(lastVisibleCardPosition);
        FrameLayout lastVisibleView = Objects.requireNonNull(lastVisibleViewVH).getBinding().cardContainerFrameLayout;
        NullPassUtil.checkFrameLayout(firstVisibleView);
        NullPassUtil.checkFrameLayout(lastVisibleView);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                slowOut(firstVisibleView, false, CARD_LOCATION_LEFT);
                slowOut(lastVisibleView, false, CARD_LOCATION_RIGHT);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                slowOut(firstVisibleView, true, CARD_LOCATION_LEFT);
                slowOut(lastVisibleView, true, CARD_LOCATION_RIGHT);
                return true;
            case DragEvent.ACTION_DROP:
                dropAndCreateService(rv, firstVisibleView, lastVisibleView);
                return true;
        }
        return false;
    }

    private void dropAndCreateService(RecyclerView containerRecyclerView,int rootCardNo, int targetContainerNo) {
        Logger.message("vm#dropAndCreateService : for empty space");
        CardDTO newCardDTO = new CardDTO.Builder().rootNo(rootCardNo).containerNo(targetContainerNo).build();
        mCardRepository.insert(newCardDTO.toEntity()
                , orderDropDataInsertListenerForEmptySpace(containerRecyclerView, targetContainerNo));
    }

    private void dropAndCreateService(RecyclerView rv, FrameLayout targetView, @Nullable FrameLayout followingView) {
        Logger.message("vm#dropAndCreateService : for card space");
        CardDTO targetCardDTO = ((ContactCardViewHolder) rv.getChildViewHolder(targetView)).getBinding().getCard();
        int targetSeqNo = targetCardDTO.getSeqNo();
        int targetRootNo = targetCardDTO.getRootNo();
        int targetContainerNo = targetCardDTO.getContainerNo();
        List<Pair<CardDTO, CardState>> targetContainerCardList = mPresentData.get(targetContainerNo);
        CardDTO newCardDTO = new CardDTO.Builder().seqNo(targetSeqNo + 1).rootNo(targetRootNo).containerNo(targetContainerNo).build();
        if (targetContainerCardList.size() > targetSeqNo + 1) {
            mCardRepository.insertAndUpdates(newCardDTO.toEntity()
                    , dtoListToEntityList(increaseListCardsSeq(targetContainerCardList, targetSeqNo + 1))
                    , orderDropDataInsertListener(targetCardDTO, targetContainerCardList, rv, targetView, followingView)
            );
        } else {
            mCardRepository.insert(newCardDTO.toEntity()
                    , orderDropDataInsertListener(targetCardDTO, targetContainerCardList, rv, targetView, followingView)
            );
        }
    }

    //{@param direction} :
    // viewLocation [left 0, right 1]
    // X coordinate [0, -screenWidth, screenWidth]
    private void slowOut(View view, boolean isReverse, int viewLocation) {
        Logger.message("vm#slowout");
        int screenWidth = ((Activity) view.getContext()).getWindowManager().getCurrentWindowMetrics().getBounds().right;
        int fromXCoordinate = -1;
        int toXCoordinate = -1;

        if (viewLocation == CARD_LOCATION_LEFT) {
            if (!isReverse) {
                fromXCoordinate = 0;
                toXCoordinate = -screenWidth;
            } else {
                fromXCoordinate = -screenWidth;
                toXCoordinate = 0;
            }
        }

        if (viewLocation == CARD_LOCATION_RIGHT) {
            if (!isReverse) {
                fromXCoordinate = 0;
                toXCoordinate = screenWidth;
            } else {
                fromXCoordinate = screenWidth;
                toXCoordinate = 0;
            }
        }

        if (fromXCoordinate == -1 || toXCoordinate == -1)
            throw new RuntimeException("slowOut/fromXCoordinate or toXCoordinate has no validated value");

        view.animate()
                .setInterpolator(AnimationUtils.loadInterpolator(view.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                .setDuration(200)
                .translationXBy(fromXCoordinate)
                .translationX(toXCoordinate).start();
    }

    /* Data operation */

    private List<CardDTO> increaseListCardsSeq(List<Pair<CardDTO, CardState>> uiList, int increaseStart) {
        Logger.message("vm#increaseListCardsSeq");
        List<CardDTO> result = new ArrayList<>();
        for (int i = increaseStart; i < uiList.size(); i++) {
            Pair<CardDTO, CardState> pair = uiList.get(i);
            pair.first.setSeqNo(pair.first.getSeqNo() + 1);
            result.add(pair.first);
        }
        return result;
    }

    private List<CardEntity> dtoListToEntityList(List<CardDTO> input) {
        Logger.message("vm#dtoListToEntityList");
        List<CardEntity> result = new ArrayList<>();
        for (CardDTO dto : input) {
            result.add(dto.toEntity());
        }
        return result;
    }

    public void loadData(OnDataLoadListener callback) {
        Logger.message("vm#loadData");
        mCardRepository.readData(() -> {
            setData();
            callback.onLoadData();
        });
    }

    private Integer[] initContainerList(List<List<CardDTO>> groupedData) {
        Logger.message("vm#initContainerList");
        List<Integer> rootNoCollector = new ArrayList<>();
        final int testSize = groupedData.size();
        int testRootNo = -1;
        for (int i = 0; i < testSize; i++) {
            List<CardDTO> dtoList = groupedData.get(i);
            boolean hasNext = false;

            if (groupedData.get(i).isEmpty()) {
                break;
            }

            for (CardDTO dto : dtoList) {
                if (testRootNo == -1 && i == 0 && dto.getSeqNo() == 0) {
                    rootNoCollector.add(dto.getRootNo());
                    testRootNo = dto.getCardNo();
                    mPresentContainerList.add(new Container());
                    hasNext = true;
                    break;
                }
                if (dto.getRootNo() == testRootNo) {
                    rootNoCollector.add(testRootNo);
                    testRootNo = dto.getCardNo();
                    mPresentContainerList.add(new Container());
                    hasNext = true;
                    break;
                }
            }

            if (!hasNext)
                break;
        }
        return rootNoCollector.toArray(new Integer[0]);
    }

    private void setData() {
        Logger.message("vm#setData");
        List<CardDTO> allDTOs = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        List<List<CardDTO>> groupedData = groupDataByContainerNo(allDTOs);
        Integer[] rootNoArr = initContainerList(groupedData);
        initContainerRootNo(rootNoArr);
        mPresentData.clear();
        mPresentData.addAll(collectPresentData(groupedData, rootNoArr));
        // for Search func
        mListLiveDataByContainer.postValue(groupedData);
        // TODO : check [following 2 line] is it redundant?
        mAllData.clear();
        mAllData.addAll(groupedData);
    }

    private void resetChildrenFocusedCardPosition(int rootContainerPosition) {
        Logger.message("vm#resetChildFocusedCardPositions");
        if (rootContainerPosition == mPresentContainerList.size() - 1)
            return;
        for (int i = rootContainerPosition + 1; i < mPresentContainerList.size(); i++) {
            mPresentContainerList.get(i).setFocusCardPosition(0);
        }
    }

    private void resetChildrenPresentContainerRootNo(int childContainerStartPosition, Integer[] childrenRootNoArr) {
        Logger.message("vm#resetContainerPresentFlags");
        Queue<Integer> childrenRootNoQueue = new LinkedList<>();
        Stream.of(childrenRootNoArr).forEach(childrenRootNoQueue::offer);
        if (childrenRootNoQueue.isEmpty())
            return;
        for (int i = childContainerStartPosition; i < mPresentContainerList.size(); i++) {
            mPresentContainerList.get(i).setRootNo(Objects.requireNonNull(childrenRootNoQueue.poll()));
        }
        if (!childrenRootNoQueue.isEmpty())
            throw new RuntimeException("vm#resetContainerPresentFlags/ after method childRootNoStack is not empty");
    }

    private void initContainerRootNo(Integer[] rootNoArr) {
        Logger.message("vm#initContainerPresentFlags");
        Queue<Integer> rootNoQueue = new LinkedList<>();
        Stream.of(rootNoArr).forEach(rootNoQueue::offer);
        for (Container container : mPresentContainerList) {
            container.setRootNo(Objects.requireNonNull(rootNoQueue.poll()));
        }
        if (!rootNoQueue.isEmpty())
            throw new RuntimeException("vm#initContainerRootNo/after work, queue is not empty.");
    }

    private List<List<CardDTO>> groupDataByContainerNo(List<CardDTO> data) {
        Logger.message("vm#groupDataByContainerNo");
        List<List<CardDTO>> valueCollector = new ArrayList<>();
        Optional.ofNullable(data).ifPresent(dtoList -> {
            for (CardDTO dto : dtoList) {
                int position = dto.getContainerNo();
                if (position > valueCollector.size() - 1) {
                    valueCollector.add(new ArrayList<>());
                }
                valueCollector.get(position).add(dto);
            }
        });
        return valueCollector;
    }

    private void resetPresentData(int rootContainerPosition, int rootCardPosition) {
        Logger.message("vm#resetPresentData");
        CardDTO rootCard = mAllData.get(rootContainerPosition).get(rootCardPosition);
        int rootNo = rootCard.getCardNo();
        final int prevPresentListSize = mPresentData.size();
        if (rootContainerPosition + 1 < prevPresentListSize) {
            mPresentData.subList(rootContainerPosition + 1, prevPresentListSize).clear();
        }

        for (int i = rootContainerPosition + 1; i < mAllData.size(); i++) {
            List<CardDTO> testList = mAllData.get(i);
            List<Pair<CardDTO, CardState>> collectingList = new ArrayList<>();
            boolean hasFound = false;
            int nextRootNo = -1;

            for (CardDTO testCard : testList) {
                if (testCard.getRootNo() != rootNo)
                    continue;
                collectingList.add(Pair.create(testCard, new CardState()));
                if (testCard.getSeqNo() == 0)
                    nextRootNo = testCard.getCardNo();
                hasFound = true;
            }
            if (!hasFound)
                break;

            mPresentData.add(collectingList);
            rootNo = nextRootNo;
        }
        Logger.message("resetPresentData / result size :" + mPresentData.size());
    }

    private void resetChildrenPresentData(int rootContainerPosition, int rootCardPosition, Integer[] childrenRootNoArr) {
        Logger.message("vm#resetChildrenPresentData");
        Queue<Integer> childrenRootNoQueue = new LinkedList<>();
        Stream.of(childrenRootNoArr).forEach(childrenRootNoQueue::offer);
        final int prevPresentListSize = mPresentData.size();
        if (rootContainerPosition + 1 < prevPresentListSize) {
            mPresentData.subList(rootContainerPosition + 1, prevPresentListSize).clear();
        }
        if (childrenRootNoQueue.isEmpty())
            return;
        for (int i = rootContainerPosition + 1; !childrenRootNoQueue.isEmpty(); i++) {
            List<CardDTO> testList = mAllData.get(i);
            List<Pair<CardDTO, CardState>> presentCardCollector = new ArrayList<>();
            for (CardDTO testCard : testList) {
                if (testCard.getRootNo() != Objects.requireNonNull(childrenRootNoQueue.peek()))
                    continue;
                presentCardCollector.add(Pair.create(testCard, new CardState()));
            }
            mPresentData.add(presentCardCollector);
            childrenRootNoQueue.poll();
        }
        if (!childrenRootNoQueue.isEmpty())
            throw new RuntimeException("vm#resetChildrenPresentData/finish work, but source queue is not empty");
        Logger.message("resetPresentData / result size :" + mPresentData.size());
    }

    private List<List<Pair<CardDTO, CardState>>> collectPresentData(List<List<CardDTO>> disorderedData, Integer[] childrenRootNoArr) {
        Logger.message("vm#collectPresentData");
        List<List<Pair<CardDTO, CardState>>> presentDataCollector = new ArrayList<>();
        Queue<Integer> childrenRootNoQueue = new LinkedList<>();
        Stream.of(childrenRootNoArr).forEach(childrenRootNoQueue::offer);
        if (childrenRootNoQueue.isEmpty())
            return presentDataCollector;
        for (int i = 0; !childrenRootNoQueue.isEmpty(); i++) {
            List<CardDTO> testList = disorderedData.get(i);
            List<Pair<CardDTO, CardState>> presentCardCollector = new ArrayList<>();
            for (CardDTO testCard : testList) {
                if (testCard.getRootNo() != Objects.requireNonNull(childrenRootNoQueue.peek()))
                    continue;
                presentCardCollector.add(Pair.create(testCard, new CardState()));
            }
            presentDataCollector.add(presentCardCollector);
            childrenRootNoQueue.poll();
        }
        if (!childrenRootNoQueue.isEmpty())
            throw new RuntimeException("vm#collectPresentData after work, queue is not empty.");
        return presentDataCollector;
    }

    public LiveData<List<List<CardDTO>>> getAllLiveData() {
        Logger.message("vm#getAllLiveData");
        return mListLiveDataByContainer;
    }

    private boolean findPresentData(List<List<Pair<CardDTO, CardState>>> basket, List<CardDTO> disorderedData, int orderFlag) {
        Logger.message("vm#findPresentData");
        List<Pair<CardDTO, CardState>> smallBasket = new ArrayList<>();
        for (CardDTO dto : disorderedData) {
            if (dto.getRootNo() == orderFlag) {
                Pair<CardDTO, CardState> cardDataPair = Pair.create(dto, new CardState(CardState.FRONT_DISPLAYING));
                smallBasket.add(cardDataPair);
            }
        }
        if (!smallBasket.isEmpty()) {
            smallBasket.sort(Comparator.comparing(p -> p.first));
            basket.add(smallBasket);
        }
        return !smallBasket.isEmpty();
    }

    /* Drop Utils*/
    public interface DropDataInsertListener extends Consumer<CardEntity> {
        void accept(CardEntity cardEntity);

        void startDropFinishAnimation();
    }

    public DropDataInsertListener orderDropDataInsertListenerForEmptySpace(RecyclerView containerRecyclerView, int targetPosition) {
        return new DropDataInsertListener() {
            @Override
            public void accept(CardEntity cardEntity) {
                mPresentContainerList.add(new Container());
                mPresentData.add(new ArrayList<>());
                CardDTO newCard = cardEntity.toDTO();
                mPresentData.get(targetPosition).add(Pair.create(newCard, new CardState()));
                if (mAllData.size() < targetPosition + 1) {
                    mAllData.add(new ArrayList<>());
                }
                mAllData.get(targetPosition).add(newCard);
                mListLiveDataByContainer.postValue(mAllData);
                ((Activity) containerRecyclerView.getContext()).runOnUiThread(() ->
                        containerRecyclerView.getAdapter().notifyItemInserted(targetPosition));
            }

            @Override
            public void startDropFinishAnimation() {

            }
        };
    }

    public DropDataInsertListener orderDropDataInsertListener(CardDTO targetDTO, List<Pair<CardDTO, CardState>> targetItemList
            , RecyclerView targetRecyclerView, View targetView, @Nullable View followingView) {
        return new DropDataInsertListener() {
            @Override
            public void accept(CardEntity foundEntity) {
                Logger.message("DropDataInsertListener#accept");
                int targetSeqNo = targetDTO.getSeqNo();
                targetItemList.add(targetSeqNo + 1, Pair.create(foundEntity.toDTO(), new CardState()));
                mAllData.get(targetDTO.getContainerNo()).add(targetSeqNo + 1, foundEntity.toDTO());
                ((Activity) targetView.getContext()).runOnUiThread(() -> {
                    Objects.requireNonNull(targetRecyclerView.getAdapter()).notifyItemInserted(targetSeqNo + 1);
                    targetRecyclerView.scrollToPosition(targetSeqNo + 1);
                    startDropFinishAnimation();
                });
            }

            @Override
            public void startDropFinishAnimation() {
                Logger.message("DropDataInsertListener#startDropFinishAnimation");
                slowOut(targetView, true, CARD_LOCATION_LEFT);
                if (Optional.ofNullable(followingView).isPresent()) {
                    slowOut(followingView, true, CARD_LOCATION_RIGHT);
                }
            }
        };
    }

    /* Container Level */

    // +1: For empty card space.
    public int presentContainerCount() {
        Logger.message("vm#presentContainerCount");
        return mPresentData.size() + 1;
    }

    /* Card Level */
    public int getPresentCardCount(int containerPosition) {
        Logger.message("vm#getPresentCardCount");
        if (containerPosition != -1) {
            synchronized (mPresentData) {
                return mPresentData.get(containerPosition).size();
            }
        }
        return 0;
    }

    public CardDTO getCardDTO(int containerPosition, int cardPosition) {
        Logger.message("vm#getCardDTO");
        return mPresentData.get(containerPosition).get(cardPosition).first;
    }

    public CardState getCardState(int containerPosition, int cardPosition) {
        Logger.message("vm#getCardState");
        return mPresentData.get(containerPosition).get(cardPosition).second;
    }

    public void updateCard(CardDTO cardDTO) {
        Logger.message("vm#updateCard");
        mCardRepository.update(cardDTO.toEntity());
    }

    @BindingAdapter("onTouchListener")
    public static void setOnTouchListener(View view, View.OnTouchListener listener) {
        view.setOnTouchListener(listener);
    }

    @BindingAdapter("onDragListener")
    public static void setOnDragListener(View view, View.OnDragListener listener) {
        view.setOnDragListener(listener);
    }

    @BindingAdapter("onLongClickListener")
    public static void setOnLongClickListener(View view, View.OnLongClickListener listener) {
        view.setOnLongClickListener(listener);
    }

    @BindingAdapter(value = {"onScrollListener", "containerPosition"})
    public static void setOnScrollListener(View view, RecyclerView.OnScrollListener listener, int containerPosition) {
        Logger.message("vm#setOnScrollListener for setLm & setContainerPosition container pos :" + containerPosition);
        RecyclerView rv = (RecyclerView) view;
        ((CardScrollListener) listener).setLayoutManager((LinearLayoutManager) rv.getLayoutManager());
        ((CardScrollListener) listener).setContainerPosition(containerPosition);
        rv.addOnScrollListener(listener);
    }

    @BindingAdapter("suppressLayout")
    public static void setSuppressLayout(View view, boolean state) {
        RecyclerView rv = (RecyclerView) view;
        rv.suppressLayout(state);
    }

    public View.OnLongClickListener getOnLongListenerForCreateCardUtilFab() {
        return onLongListenerForCreateCardUtilFab;
    }

    public View.OnDragListener getOnDragListenerForCardRecyclerView() {
        return onDragListenerForCardRecyclerView;
    }

    public View.OnDragListener getOnDragListenerForEmptyCardSpace() {
        return onDragListenerForEmptyCardSpace;
    }

    public void presentChildren(RecyclerView cardRecyclerView, int rootContainerPosition, int rootCardPosition) {
        Logger.message("vm#presentChildren/ rootContainerPosition :" + rootContainerPosition + "/rootCardPosition" + rootCardPosition);
        final int prevPresentContainerSize = mPresentContainerList.size();
        Integer[] childrenRootNoArr = resetPresentContainerList(rootContainerPosition, rootCardPosition);
        resetChildrenPresentContainerRootNo(rootContainerPosition + 1, childrenRootNoArr);
        resetChildrenFocusedCardPosition(rootContainerPosition);
        resetChildrenPresentData(rootContainerPosition, rootCardPosition, childrenRootNoArr);
        notifyContainerItemChanged(((RecyclerView) cardRecyclerView.getParent().getParent()), getContainerAdapterFromCardRecyclerView(cardRecyclerView)
                , prevPresentContainerSize, mPresentData.size()
                , rootContainerPosition);
    }

    private Integer[] resetPresentContainerList(int rootContainerPosition, int rootCardPosition) {
        Logger.message("vm#resetPresentContainerList");
        List<Integer> rootNoCollector = new ArrayList<>();
        final int prevLastPosition = mPresentContainerList.size() - 1;
        if (prevLastPosition > rootContainerPosition) {
            mPresentContainerList.subList(rootContainerPosition + 1, prevLastPosition + 1).clear();
        }
        int testRootNo = mAllData.get(rootContainerPosition).get(rootCardPosition).getCardNo();
        for (int i = rootContainerPosition + 1; i < mAllData.size(); i++) {
            List<CardDTO> testList = mAllData.get(i);
            boolean hasFound = false;
            for (CardDTO dto : testList) {
                if (dto.getRootNo() == testRootNo && dto.getSeqNo() == 0) {
                    mPresentContainerList.add(new Container());
                    rootNoCollector.add(testRootNo);
                    testRootNo = dto.getCardNo();
                    hasFound = true;
                    break;
                }
            }
            if (!hasFound)
                break;
        }
        return rootNoCollector.toArray(new Integer[0]);
    }

    private void notifyContainerItemChanged(RecyclerView containerRecyclerView, ContainerAdapter containerAdapter, int prevContainerSize, int nextContainerSize, int rootContainerPosition) {
        Logger.message("vm#notifyContainerItemChanged");
        if (containerRecyclerView.isComputingLayout()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() ->
                            notifyContainerItemChanged(containerRecyclerView, containerAdapter, prevContainerSize, nextContainerSize, rootContainerPosition)
                    , 100);
            return;
        }
        if (rootContainerPosition + 1 == prevContainerSize && rootContainerPosition + 1 == nextContainerSize)
            return;

        if (prevContainerSize < nextContainerSize) {
            if (prevContainerSize > rootContainerPosition + 1) {
                int changeCount = prevContainerSize - (rootContainerPosition + 1);
                containerAdapter.notifyItemRangeChanged(rootContainerPosition + 1, changeCount);
            }
            int insertCount = nextContainerSize - prevContainerSize;
            containerAdapter.notifyItemRangeInserted(prevContainerSize, insertCount);
            return;
        }

        if (prevContainerSize > nextContainerSize) {
            if (prevContainerSize > rootContainerPosition + 1) {
                int changeCount = prevContainerSize - (rootContainerPosition + 1);
                containerAdapter.notifyItemRangeChanged(rootContainerPosition + 1, changeCount);
            }
            int removeCount = prevContainerSize - nextContainerSize;
            containerAdapter.notifyItemRangeRemoved(nextContainerSize, removeCount);
            return;
        }

        int changeCount = prevContainerSize - (rootContainerPosition + 1);
        containerAdapter.notifyItemRangeChanged(rootContainerPosition + 1, changeCount);

    }

    private ContainerAdapter getContainerAdapterFromCardRecyclerView(RecyclerView cardRecyclerView) {
        Logger.message("vm#getContainerAdapterFromCardRecyclerView : util");
        return (ContainerAdapter) getContainerRecyclerViewFromCardRecyclerView(cardRecyclerView).getAdapter();
    }

    private RecyclerView getContainerRecyclerViewFromCardRecyclerView(RecyclerView cardRecyclerView) {
        Logger.message("vm#getContainerRecyclerViewFromCardRecyclerView : util");
        return (RecyclerView) cardRecyclerView.getParent().getParent();
    }


    public RecyclerView.OnScrollListener getOnScrollListenerForCardRecyclerView() {
        Logger.message("vm#getOnScrollListenerForCardRecyclerView for data binding");
        CardScrollListener.OnFocusChangedListener onFocusChangedListener = new CardScrollListener.OnFocusChangedListener() {
            @Override
            public void onNextFocused(RecyclerView view, int containerPosition, int cardPosition) {
                synchronized (mPresentData) {
                    mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                    presentChildren(view, containerPosition, cardPosition);
                }
            }

            @Override
            public void onPreviousFocused(RecyclerView view, int containerPosition, int cardPosition) {
                synchronized (mPresentData) {
                    mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                    presentChildren(view, containerPosition, cardPosition);
                }
            }
        };
        CardScrollListener.OnScrollStateChangeListener onScrollStateChangeListener = new CardScrollListener.OnScrollStateChangeListener() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onStateIdle(RecyclerView view, int containerPosition) {
                synchronized (mPresentContainerList) {
                    handler.postDelayed(() -> {
                        for (Container container : mPresentContainerList) {
                            container.setLayoutSuppressed(false);
                        }
                    }, 500);
                }
            }

            @Override
            public void onStateDragging(RecyclerView view, int containerPosition) {
                synchronized (mPresentContainerList) {
                    for (int i = 0; i < mPresentContainerList.size(); i++) {
                        Container container = mPresentContainerList.get(i);
                        if (i == containerPosition) {
                            container.setLayoutSuppressed(false);
                            continue;
                        }
                        container.setLayoutSuppressed(true);
                    }
                }
            }
        };
        return new CardScrollListener(onFocusChangedListener, onScrollStateChangeListener);
    }

    public List<List<Pair<CardDTO, CardState>>> getPresentData() {
        Logger.message("vm#getPresentData");
        return mPresentData;
    }

    public Container getContainer(int containerPosition) {
        return mPresentContainerList.get(containerPosition);
    }

}