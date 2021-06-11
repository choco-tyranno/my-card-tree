package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pair;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CloneCardShadow;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.Container;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    private final MutableLiveData<List<List<CardDTO>>> mLiveData;
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
        this.mLiveData = new MutableLiveData<>();
        this.mAllData = new ArrayList<>();
        this.mPresentData = new ArrayList<>();
        this.mPresentContainerList = new ArrayList<>();
        this.onLongListenerForCreateCardUtilFab = (view) -> view.startDragAndDrop(ClipData.newPlainText("", ""), new CardShadow(view), "CREATE", 0);
        initCardRecyclerViewDragListener();
        initEmptyCardSpaceDragListener();
    }

    /* remove card*/

    public void onRemoveBtnClicked(View view, CardDTO targetCard) {
        int targetContainerPosition = findContainerPositionByRemoveBtn(view);
        List<CardDTO> removeItemList = new ArrayList<>(findChildrenCards(targetCard, targetContainerPosition));
        removeItemList.add(targetCard);
        alertDeleteWarningDialog(view, targetCard, removeItemList, targetContainerPosition);
    }

    private void removeFromAllList(CardDTO[] removeItemArr, int targetContainerPosition) {
        Queue<CardDTO> removeItemQueue = new LinkedList<>();
        Stream.of(removeItemArr).forEach(removeItemQueue::offer);
        Logger.hotfixMessage("[before] removeItemQueue size :" + removeItemQueue.size());
        HashMap<Integer, Queue<CardDTO>> containerPositionMap = new HashMap<>();
        while (!removeItemQueue.isEmpty()) {
            CardDTO testCard = removeItemQueue.poll();
            int testContainerNo = Objects.requireNonNull(testCard).getContainerNo();
            if (!containerPositionMap.containsKey(testContainerNo)) {
                containerPositionMap.put(testCard.getContainerNo(), new LinkedList<>());
            }
            Objects.requireNonNull(containerPositionMap.get(testContainerNo)).offer(testCard);
        }
        Logger.hotfixMessage("#removeFromAllList/fin while loop. map size : " + containerPositionMap.size());

        for (int i = targetContainerPosition; i < mAllData.size(); i++) {
            if (!containerPositionMap.containsKey(i))
                break;
            Queue<CardDTO> testQueue = containerPositionMap.get(i);
            while (!Objects.requireNonNull(testQueue).isEmpty()) {
                mAllData.get(i).remove(testQueue.poll());
            }
        }
    }

    private void handleRemoveOneLeftTargetCard(View view, CardDTO cardDTO, List<CardDTO> removeItemList, int targetContainerPosition) {
        CardDTO[] removeItemArr = removeItemList.toArray(new CardDTO[0]);
        final int removeContainerCount = mPresentData.size() - (targetContainerPosition - 1 + 1);
        mCardRepository.delete(
                dtoListToEntityList(removeItemList)
                , (deleteCount) -> {
                    if (deleteCount != removeItemArr.length) {
                        runOnUiThreadByView(view, () -> MySuperToast.showTextShort(view.getContext(), "삭제요청 실패. 잠시후 다시 시도해주세요"));
                        return;
                    }
                    removeFromAllList(removeItemArr, targetContainerPosition);
                    mLiveData.postValue(mAllData);
                    mPresentData.subList(targetContainerPosition, mPresentData.size()).clear();
                    mPresentContainerList.subList(targetContainerPosition, mPresentContainerList.size()).clear();
                    RecyclerView containerRecyclerView = getContainerRecyclerViewFromRemoveButton(view);
                    runOnUiThreadByView(view, () -> {
                        Objects.requireNonNull(containerRecyclerView.getAdapter()).notifyItemRangeRemoved(targetContainerPosition, removeContainerCount);
                        MySuperToast.showTextShort(view.getContext(), "요청한 카드가 삭제되었습니다.");
                    });

                }
        );
    }

    private boolean isFocusedItem(int targetContainerPosition, int targetCardPosition) {
        return mPresentContainerList.get(targetContainerPosition).getFocusCardPosition() == targetCardPosition;
    }

    private int findNearestItemPosition(int targetContainerPosition, int testCardPosition) {
        final int presentItemCount = mPresentData.get(targetContainerPosition).size();
        if (presentItemCount < testCardPosition + 1)
            return presentItemCount - 1;
        return testCardPosition;
    }

    //updateItemList collecting with seq update.
    private void handleRemoveTargetCardInCrowds(View view, CardDTO cardDTO, List<CardDTO> removeItemList, List<CardDTO> updateItemList, int targetContainerPosition) {
        CardDTO[] removeItemArr = removeItemList.toArray(new CardDTO[0]);
        Logger.hotfixMessage("before work : updateItemList : " + updateItemList.size());
        Logger.hotfixMessage("before work : removeItemList : " + removeItemList.size());
        for (CardDTO updateCard : updateItemList) {
            updateCard.setSeqNo(updateCard.getSeqNo() - 1);
        }
        boolean focusedTarget = isFocusedItem(targetContainerPosition, cardDTO.getSeqNo());
        mCardRepository.deleteAndUpdate(
                dtoListToEntityList(removeItemList)
                , dtoListToEntityList(updateItemList)
                , (deleteCount) -> {
                    if (deleteCount != removeItemArr.length) {
                        Logger.hotfixMessage("deleteCount:" + deleteCount);
                        Logger.hotfixMessage("removeItemArr.length:" + removeItemArr.length);
                        runOnUiThreadByView(view, () -> MySuperToast.showTextShort(view.getContext(), "삭제요청 실패. 잠시후 다시 시도해주세요"));
                        return;
                    }
                    removeFromAllList(removeItemArr, targetContainerPosition);
                    mLiveData.postValue(mAllData);
                    mPresentData.get(targetContainerPosition).remove(cardDTO.getSeqNo());
                    if (focusedTarget) {
                        runOnUiThreadByView(view, () -> {
                            RecyclerView targetCardRecyclerView = getCardRecyclerViewFromRemoveButton(view);
                            targetCardRecyclerView.getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
                            int newFocusPosition = findNearestItemPosition(targetContainerPosition, cardDTO.getSeqNo());
                            mPresentContainerList.get(targetContainerPosition).setFocusCardPosition(newFocusPosition);
                            presentChildren(targetCardRecyclerView, targetContainerPosition, newFocusPosition);
                            MySuperToast.showTextShort(view.getContext(), "요청한 카드가 삭제되었습니다.");
                        });
                    } else {
                        runOnUiThreadByView(view, () -> {
                            getCardRecyclerViewFromRemoveButton(view).getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
                            MySuperToast.showTextShort(view.getContext(), "요청한 카드가 삭제되었습니다.");
                        });
                    }
                }
        );
    }


    private void runOnUiThreadByView(View view, Runnable action) {
        ((Activity) view.getContext()).runOnUiThread(action);
    }

    private boolean checkOneLeftCard(int targetContainerPosition) {
        return mPresentData.get(targetContainerPosition).size() == 1;
    }

    private int findContainerPositionByRemoveBtn(View view) {
        ConstraintLayout containerLayout = (ConstraintLayout) view.getParent().getParent().getParent().getParent().getParent();
        RecyclerView containerRecyclerView = (RecyclerView) containerLayout.getParent();
        return containerRecyclerView.getChildAdapterPosition(containerLayout);
    }

    private int findCardPositionByRemoveBtn(View view) {
        FrameLayout cardFrameLayout = (FrameLayout) view.getParent().getParent().getParent();
        RecyclerView cardRecyclerView = (RecyclerView) cardFrameLayout.getParent();
        return cardRecyclerView.getChildAdapterPosition(cardFrameLayout);
    }


    private List<CardDTO> findChildrenCards(CardDTO rootCard, int rootContainerPosition) {
        List<CardDTO> foundChildrenCardCollector = new ArrayList<>();
        int testRootNo = rootCard.getCardNo();
        int childContainerPosition = rootContainerPosition + 1;
        if (mAllData.size() >= childContainerPosition + 1) {
            List<CardDTO> testList = mAllData.get(rootContainerPosition + 1);
            for (CardDTO testCard : testList) {
                if (testCard.getRootNo() != testRootNo)
                    continue;
                foundChildrenCardCollector.add(testCard);
                foundChildrenCardCollector.addAll(findChildrenCards(testCard, childContainerPosition));
            }
        }
        return foundChildrenCardCollector;
    }

    private void alertDeleteWarningDialog(View view, CardDTO targetCardDTO, List<CardDTO> removeItemList, int targetContainerPosition) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());
        String targetTitle = targetCardDTO.getTitle();
        if (TextUtils.equals(targetTitle, "")) {
            targetTitle = "이름 미지정";
        }

        alertBuilder.setTitle("-카드 제거-")
                .setMessage(" 선택된 <" + targetTitle + "> 카드와 함께,\n관련된 하위 '" + (removeItemList.size() - 1) + "'개 카드를 지우시겠습니까?"
                        + "\n-> 총 " + removeItemList.size() + "개")
                .setCancelable(false)
                .setPositiveButton("제거", (dialog, which) -> {
                    boolean isOneLeftCard = checkOneLeftCard(targetContainerPosition);
                    if (isOneLeftCard)
                        handleRemoveOneLeftTargetCard(view, targetCardDTO, removeItemList, targetContainerPosition);
                    else {
                        findUpdateItems(targetContainerPosition, targetCardDTO.getSeqNo());
                        handleRemoveTargetCardInCrowds(view, targetCardDTO
                                , removeItemList
                                , findUpdateItems(targetContainerPosition, targetCardDTO.getSeqNo())
                                , targetContainerPosition);
                    }
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    MySuperToast.showTextShort(view.getContext(), "요청이 취소됐습니다.");
                    dialog.cancel();
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private List<CardDTO> findUpdateItems(int targetContainerPosition, int targetCardPosition) {
        List<CardDTO> foundItems = new ArrayList<>();
        List<Pair<CardDTO, CardState>> updateList =
                mPresentData.get(targetContainerPosition).subList(targetCardPosition + 1, mPresentData.get(targetContainerPosition).size());
        for (Pair<CardDTO, CardState> pair : updateList) {
            foundItems.add(pair.first);
        }
        return foundItems;
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
        MySuperToast.showTextShort(view.getContext(), "" + isOn);
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
            String dragType = (String) event.getLocalState();
            if (TextUtils.equals(dragType, "CREATE")){
                return handleCreateService(view, event);
            }
            if (TextUtils.equals(dragType, "MOVE")){
                return handleMoveService(view);
            }
            return false;

        };
    }

    private boolean handleMoveService(View view){
        MySuperToast.showTextShort(view.getContext(), "move");
        return false;
    }

    private boolean handleCreateService(View view, DragEvent event){
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
    }

    private void initCardRecyclerViewDragListener() {
        Logger.message("vm#initCardRecyclerViewDragListener");
        onDragListenerForCardRecyclerView = (view, event) -> {
            if (view instanceof RecyclerView) {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    return true;
                }
                String dragType = (String) event.getLocalState();
                RecyclerView targetView = (RecyclerView) view;
                if (TextUtils.equals(dragType, "CREATE")){
                    return handleCreateService(targetView, event);
                }
                if (TextUtils.equals(dragType, "MOVE")){
                    return handleMoveService(targetView);
                }
            } else {
                throw new RuntimeException("#ondrag() : recyclerview not found");
            }
            return false;
        };
    }

    private boolean handleMoveService(RecyclerView targetView){
        MySuperToast.showTextShort(targetView.getContext(), "move");
        return false;
    }

    private boolean handleCreateService(RecyclerView targetView, DragEvent event){
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

    //Null in lastVisibleViewVH
    private boolean handleDragEventMultiItemVisibleCase(RecyclerView rv, int firstVisibleCardPosition, int lastVisibleCardPosition, DragEvent event) {
        Logger.message("vm#handleDragEventMultiItemVisibleCase");
        ContactCardViewHolder firstVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(firstVisibleCardPosition);
        ContactCardViewHolder lastVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(lastVisibleCardPosition);
        if (firstVisibleViewVH==null||lastVisibleViewVH==null) {
            return false;
        }
        FrameLayout firstVisibleView = Objects.requireNonNull(firstVisibleViewVH).getBinding().cardContainerFrameLayout;
        FrameLayout lastVisibleView = Objects.requireNonNull(lastVisibleViewVH).getBinding().cardContainerFrameLayout;
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

    //Drop target view is recyclerView.
    //single card case + in crowds case.
    private void dropAndCreateService(RecyclerView rv, FrameLayout prevSeqCardView, @Nullable FrameLayout nextSeqCardView) {
        Logger.message("vm#dropAndCreateService : for card space");
        CardDTO prevSeqCardDTO = ((ContactCardViewHolder) rv.getChildViewHolder(prevSeqCardView)).getBinding().getCard();
        int prevCardSeqNo = prevSeqCardDTO.getSeqNo();
        int rootNo = prevSeqCardDTO.getRootNo();
        int containerNo = prevSeqCardDTO.getContainerNo();
        List<Pair<CardDTO, CardState>> targetContainerCardList = mPresentData.get(containerNo);
        CardDTO newCardDTO = new CardDTO.Builder().seqNo(prevCardSeqNo + 1).rootNo(rootNo).containerNo(containerNo).build();
        if (targetContainerCardList.size() > prevCardSeqNo + 1) {
            mCardRepository.insertAndUpdates(newCardDTO.toEntity()
                    , dtoListToEntityList(increaseListCardsSeq(targetContainerCardList, prevCardSeqNo + 1))
                    , orderDropDataInsertListener(prevSeqCardDTO, targetContainerCardList, rv, prevSeqCardView, nextSeqCardView)
            );
        } else {
            mCardRepository.insert(newCardDTO.toEntity()
                    , orderDropDataInsertListener(prevSeqCardDTO, targetContainerCardList, rv, prevSeqCardView, nextSeqCardView)
            );
        }
    }

    //Drop target view is empty space view.
    private void dropAndCreateService(RecyclerView containerRecyclerView, int rootCardNo, int targetContainerNo) {
        Logger.message("vm#dropAndCreateService : for empty space");
        CardDTO newCardDTO = new CardDTO.Builder().rootNo(rootCardNo).containerNo(targetContainerNo).build();
        mCardRepository.insert(newCardDTO.toEntity()
                , orderDropDataInsertListenerForEmptySpace(containerRecyclerView, targetContainerNo));
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
        for (List<Pair<CardDTO, CardState>> presentContainerItem : mPresentData) {
            presentContainerItem.sort((a, b) -> a.first.compareTo(b.first));
        }
        // for Search func
        mLiveData.postValue(groupedData);
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

    private void resetChildrenPresentData(int rootContainerPosition, Integer[] childrenRootNoArr) {
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
        Logger.message("resetPresentData / result size :" + mPresentData.size());
    }

    private List<List<Pair<CardDTO, CardState>>> collectPresentData(List<List<CardDTO>> disorderedData, Integer[] rootNoArr) {
        Logger.message("vm#collectPresentData");
        List<List<Pair<CardDTO, CardState>>> presentDataCollector = new ArrayList<>();
        Queue<Integer> childrenRootNoQueue = new LinkedList<>();
        Stream.of(rootNoArr).forEach(childrenRootNoQueue::offer);
        Logger.hotfixMessage("rootNo seq check");
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
        return presentDataCollector;
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
                mLiveData.postValue(mAllData);
                runOnUiThreadByView(containerRecyclerView, () ->
                        Objects.requireNonNull(containerRecyclerView.getAdapter()).notifyItemInserted(targetPosition));
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

    public View.OnLongClickListener getOnLongClickListenerForCard(){
        return (view)->{
            LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View cloneCardView = layoutInflater.inflate(R.layout.item_card_front_clone, null, false);
            cloneCardView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            cloneCardView.layout(0, 0, (int)view.getResources().getDimension(R.dimen.cloneCard_width),(int)view.getResources().getDimension(R.dimen.cloneCard_height));
            return view.startDragAndDrop(ClipData.newPlainText("", ""), new CloneCardShadow(cloneCardView), "MOVE", 0);
        };
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
        resetChildrenPresentData(rootContainerPosition, childrenRootNoArr);
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

    private RecyclerView getContainerRecyclerViewFromRemoveButton(View view) {
        return (RecyclerView) view.getParent().getParent().getParent().getParent().getParent().getParent();
    }

    private RecyclerView getCardRecyclerViewFromRemoveButton(View view) {
        return (RecyclerView) view.getParent().getParent().getParent().getParent();
    }

    public RecyclerView.OnScrollListener getOnScrollListenerForCardRecyclerView() {
        Logger.message("vm#getOnScrollListenerForCardRecyclerView for data binding");
        CardScrollListener.OnFocusChangedListener onFocusChangedListener = new CardScrollListener.OnFocusChangedListener() {
            @Override
            public void onNextFocused(RecyclerView view, int containerPosition, int cardPosition) {
                synchronized (mPresentData) {
                    Logger.hotfixMessage("[container :" + containerPosition + "] onNext cardPos:" + cardPosition);
                    mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                    presentChildren(view, containerPosition, cardPosition);
                }
            }

            @Override
            public void onPreviousFocused(RecyclerView view, int containerPosition, int cardPosition) {
                synchronized (mPresentData) {
                    Logger.hotfixMessage("[container :" + containerPosition + "] onPrev cardPos:" + cardPosition);
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

    public Container getContainer(int containerPosition) {
        return mPresentContainerList.get(containerPosition);
    }

    public List<Pair<CardDTO, CardState>> getTargetPositionPresentData(int containerPos) {
        return mPresentData.get(containerPos);
    }

}