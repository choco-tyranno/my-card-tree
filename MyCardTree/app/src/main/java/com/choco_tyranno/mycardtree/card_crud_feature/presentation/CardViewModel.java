package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardLongClickListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardScrollListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ArrowPresenter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.Container;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CardViewModel extends AndroidViewModel implements UiThreadAccessible {
    private final CardRepository mCardRepository;
    private final MutableLiveData<List<List<CardDTO>>> mLiveData;
    private List<List<CardDTO>> mAllData;
    private final List<Container> mPresentContainerList;
    private final List<List<Pair<CardDTO, CardState>>> mPresentData;
    private boolean computingLayout;

    private final View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    private View.OnDragListener onDragListenerForCardRecyclerView;
    private View.OnDragListener onDragListenerForEmptyCardSpace;
    private CardScrollListener.OnFocusChangedListener mOnFocusChangedListener;
    private CardScrollListener.OnScrollStateChangeListener mOnScrollStateChangeListener;

    private final int CARD_LOCATION_LEFT = 0;
    private final int CARD_LOCATION_RIGHT = 1;

    /* Default constructor*/

    public CardViewModel(Application application) {
        super(application);
        Logger.message("VM#constructor");
        this.mCardRepository = new CardRepository(application);
        this.mLiveData = new MutableLiveData<>();
        this.mAllData = new ArrayList<>();
        this.mPresentData = new ArrayList<>();
        this.mPresentContainerList = new ArrayList<>();
        this.onLongListenerForCreateCardUtilFab = (view) -> view.startDragAndDrop(ClipData.newPlainText("", ""), new CardShadow(view), "CREATE", 0);
        this.computingLayout = false;
        initCardRecyclerViewDragListener();
        initEmptyCardSpaceDragListener();
        initOnFocusChangedListener();
        initOnScrollStateChangeListener();
    }

    public boolean isComputingLayout() {
        return computingLayout;
    }

    public void setComputingLayout(boolean computingLayout) {
        this.computingLayout = computingLayout;
    }

    private void initOnScrollStateChangeListener() {
        mOnScrollStateChangeListener = (savedScrollState, containerPosition) -> {
            if (mPresentContainerList.size() > containerPosition) {
                mPresentContainerList.get(containerPosition).setSavedScrollState(savedScrollState);
            }
        };
    }

    public CardScrollListener.OnScrollStateChangeListener getOnScrollStateChangeListener() {
        return mOnScrollStateChangeListener;
    }

    private void initOnFocusChangedListener() {
        mOnFocusChangedListener = new CardScrollListener.OnFocusChangedListener() {
            @Override
            public void onNextFocused(RecyclerView view, int containerPosition, int cardPosition) {
                synchronized (mPresentData) {
                    Logger.message("[container :" + containerPosition + "] onNext cardPos:" + cardPosition);
                    mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                    presentChildren(view, containerPosition, cardPosition);
                }
            }

            @Override
            public void onPreviousFocused(RecyclerView view, int containerPosition, int cardPosition) {
                synchronized (mPresentData) {
                    Logger.message("[container :" + containerPosition + "] onPrev cardPos:" + cardPosition);
                    mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                    presentChildren(view, containerPosition, cardPosition);
                }
            }
        };
    }


    /* remove card */

    public void onRemoveBtnClicked(View view, CardDTO targetCard) {
        int targetContainerPosition = findContainerPositionByRemoveBtn(view);
        List<CardDTO> removeItemList = new ArrayList<>(findChildrenCards(targetCard, targetContainerPosition));
        removeItemList.add(targetCard);
        alertDeleteWarningDialog(view, targetCard, removeItemList, targetContainerPosition);
    }

    private void removeFromAllList(CardDTO[] removeItemArr, int targetContainerPosition) {
        Queue<CardDTO> removeItemQueue = new LinkedList<>();
        Stream.of(removeItemArr).forEach(removeItemQueue::offer);
        Logger.message("[before] removeItemQueue size :" + removeItemQueue.size());
        HashMap<Integer, Queue<CardDTO>> containerPositionMap = new HashMap<>();
        while (!removeItemQueue.isEmpty()) {
            CardDTO testCard = removeItemQueue.poll();
            int testContainerNo = Objects.requireNonNull(testCard).getContainerNo();
            if (!containerPositionMap.containsKey(testContainerNo)) {
                containerPositionMap.put(testCard.getContainerNo(), new LinkedList<>());
            }
            Objects.requireNonNull(containerPositionMap.get(testContainerNo)).offer(testCard);
        }
        Logger.message("#removeFromAllList/fin while loop. map size : " + containerPositionMap.size());

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
                        runOnUiThread(() -> SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "삭제요청 실패. 잠시후 다시 시도해주세요")), view.getContext());
                        return;
                    }
                    removeFromAllList(removeItemArr, targetContainerPosition);
                    mLiveData.postValue(mAllData);
                    mPresentData.subList(targetContainerPosition, mPresentData.size()).clear();
                    mPresentContainerList.subList(targetContainerPosition, mPresentContainerList.size()).clear();
                    RecyclerView containerRecyclerView = getContainerRecyclerViewFromRemoveButton(view);
                    runOnUiThread(() -> {
                        Objects.requireNonNull(containerRecyclerView.getAdapter()).notifyItemRangeRemoved(targetContainerPosition, removeContainerCount);
                        SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "요청한 카드가 삭제되었습니다."));
                    }, view.getContext());

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
        Logger.message("before work : updateItemList : " + updateItemList.size());
        Logger.message("before work : removeItemList : " + removeItemList.size());
        for (CardDTO updateCard : updateItemList) {
            updateCard.setSeqNo(updateCard.getSeqNo() - 1);
        }
        boolean focusedTarget = isFocusedItem(targetContainerPosition, cardDTO.getSeqNo());
        mCardRepository.deleteAndUpdate(
                dtoListToEntityList(removeItemList)
                , dtoListToEntityList(updateItemList)
                , (deleteCount) -> {
                    if (deleteCount != removeItemArr.length) {
                        Logger.message("deleteCount:" + deleteCount);
                        Logger.message("removeItemArr.length:" + removeItemArr.length);
                        runOnUiThread(() -> SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "삭제요청 실패. 잠시후 다시 시도해주세요")), view.getContext());
                        return;
                    }
                    removeFromAllList(removeItemArr, targetContainerPosition);
                    mLiveData.postValue(mAllData);
                    mPresentData.get(targetContainerPosition).remove(cardDTO.getSeqNo());
                    if (focusedTarget) {
                        runOnUiThread(() -> {
                            RecyclerView targetCardRecyclerView = getCardRecyclerViewFromRemoveButton(view);
                            targetCardRecyclerView.getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
                            int newFocusPosition = findNearestItemPosition(targetContainerPosition, cardDTO.getSeqNo());
                            mPresentContainerList.get(targetContainerPosition).setFocusCardPosition(newFocusPosition);
                            presentChildren(targetCardRecyclerView, targetContainerPosition, newFocusPosition);
                            SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "요청한 카드가 삭제되었습니다."));
                        }, view.getContext());
                    } else {
                        runOnUiThread(() -> {
                            getCardRecyclerViewFromRemoveButton(view).getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
                            SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "요청한 카드가 삭제되었습니다."));
                        }, view.getContext());
                    }
                }
        );
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
                    SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "요청이 취소됐습니다."));
                    dialog.cancel();
                });
        AlertDialog alertDialog = alertBuilder.create();
        runOnUiThread(alertDialog::show, view.getContext());
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
    }

    /* Drag and drop for add new card*/

    private static final int CARD_RECYCLERVIEW = 1;
    private static final int EMPTY_CARD_SPACE = 2;

    public void initEmptyCardSpaceDragListener() {
        Logger.message("vm#initEmptyCardSpaceDragListener");
        onDragListenerForEmptyCardSpace = (view, event) -> {
            if (!(view instanceof TextView)) {
                return false;
            }
            if (event.getAction() == DragEvent.ACTION_DRAG_STARTED)
                return true;
            String dragType = (String) event.getLocalState();
            if (TextUtils.equals(dragType, "CREATE")) {
                return handleCreateServiceForEmptySpace(view, event);
            }
            if (TextUtils.equals(dragType, "MOVE")) {
                return handleMoveServiceForEmptySpace((TextView) view, event);
            }
            return false;

        };
    }


    private boolean handleCreateServiceForEmptySpace(View view, DragEvent event) {
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
            dropAndCreateServiceForEmptySpace(containerRecyclerView, rootCardNo, targetContainerPosition);
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
                if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                    return true;
                }
                String dragType = (String) event.getLocalState();
                RecyclerView targetView = (RecyclerView) view;
                if (TextUtils.equals(dragType, "CREATE")) {
                    return handleCreateServiceForContainer(targetView, event);
                }
                if (TextUtils.equals(dragType, "MOVE")) {
                    return handleMoveServiceForCardRecyclerView(targetView, event);
                }
            } else {
                throw new RuntimeException("#ondrag() : recyclerview not found");
            }
            return false;
        };
    }

    private boolean handleMoveServiceForEmptySpace(TextView targetView, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            case DragEvent.ACTION_DROP:
        }
        return false;
    }

    private boolean handleMoveServiceForCardRecyclerView(RecyclerView targetView, DragEvent event) {
        int containerPosition = ((CardAdapter) targetView.getAdapter()).getPosition();
        if (containerPosition == -1)
            return false;
        Container container = mPresentContainerList.get(containerPosition);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                ArrowPresenter.fadeInArrowsIfNecessary(
                        ArrowPresenter.CARD_RECYCLERVIEW
                        , targetView
                        , container);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                ArrowPresenter.fadeOutArrowsIfNecessary(targetView, container);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
//                ArrowPresenter.fadeOutArrowsIfNecessary();
                break;
            case DragEvent.ACTION_DROP:
                break;
        }
        return false;
    }

    private boolean handleCreateServiceForContainer(RecyclerView targetView, DragEvent event) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) targetView.getLayoutManager();
        if (!(NullPassUtil.checkLinearLayoutManager(layoutManager).getItemCount() > 0)) {
            return false;
        }
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        Logger.message("#handleCreateService - firstVisibleItemPosition :" + firstVisibleItemPosition
                + "lastVisibleItemPosition" + lastVisibleItemPosition);
        if (firstVisibleItemPosition == lastVisibleItemPosition) {
            return handleDragEventSingleItemVisibleCase(targetView, firstVisibleItemPosition, event);
        }
        return handleDragEventMultiItemVisibleCase(targetView, firstVisibleItemPosition, lastVisibleItemPosition, event);
    }

    private boolean handleDragEventSingleItemVisibleCase(RecyclerView rv, int targetCardPosition, DragEvent event) {
        Logger.message("vm#handleDragEventSingleItemVisibleCase");
        FrameLayout targetView = NullPassUtil.checkFrameLayout(
                Objects.requireNonNull(
                        (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(targetCardPosition)
                ).getBinding().cardContainerFrameLayout);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                slowOut(targetView, false, CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                slowOut(targetView, true, CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DROP:
                slowOut(targetView, true, CARD_LOCATION_LEFT);
                dropAndCreateServiceForContainer(rv, targetView);
                return true;
        }
        return false;
    }

    //Null in lastVisibleViewVH
    private boolean handleDragEventMultiItemVisibleCase(RecyclerView rv, int firstVisibleCardPosition, int lastVisibleCardPosition, DragEvent event) {
        Logger.message("vm#handleDragEventMultiItemVisibleCase");
        ContactCardViewHolder firstVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(firstVisibleCardPosition);
        ContactCardViewHolder lastVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(lastVisibleCardPosition);
        if (firstVisibleViewVH == null || lastVisibleViewVH == null) {
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
                slowOut(firstVisibleView, true, CARD_LOCATION_LEFT);
                slowOut(lastVisibleView, true, CARD_LOCATION_RIGHT);
                dropAndCreateServiceForContainer(rv, firstVisibleView);
                return true;
        }
        return false;
    }

    //Drop target view is recyclerView.
    //single card case + in crowds case.
    private void dropAndCreateServiceForContainer(RecyclerView rv, FrameLayout prevSeqCardView) {
        Logger.message("vm#dropAndCreateService : for card space");
        ItemCardFrameBinding cardFrameBinding = ((ContactCardViewHolder) rv.getChildViewHolder(prevSeqCardView)).getBinding();
        CardDTO prevSeqCardDTO = cardFrameBinding.getCard();
        CardState prevCardState = cardFrameBinding.getCardState();
        int prevCardSeqNo = prevSeqCardDTO.getSeqNo();
        int rootNo = prevSeqCardDTO.getRootNo();
        int containerNo = prevSeqCardDTO.getContainerNo();
        List<Pair<CardDTO, CardState>> targetContainerCardList = mPresentData.get(containerNo);
        CardDTO newCardDTO = new CardDTO.Builder().seqNo(prevCardSeqNo + 1).rootNo(rootNo).containerNo(containerNo).build();
        if (targetContainerCardList.size() > prevCardSeqNo + 1) {
            mCardRepository.insertAndUpdates(
                    newCardDTO.toEntity()
                    , dtoListToEntityList(
                            increaseListCardsSeq(targetContainerCardList, prevCardSeqNo + 1))
                    , orderDropDataInsertListenerForContainer(prevSeqCardDTO, prevCardState, targetContainerCardList, rv)
            );
        } else {
            mCardRepository.insert(newCardDTO.toEntity()
                    , orderDropDataInsertListenerForContainer(prevSeqCardDTO, prevCardState, targetContainerCardList, rv)
            );
        }
    }

    //Drop target view is empty space view.
    private void dropAndCreateServiceForEmptySpace(RecyclerView containerRecyclerView, int rootCardNo, int targetContainerNo) {
        Logger.message("vm#dropAndCreateService : for empty space");
        SwitchMaterial removeBtn = ((View) containerRecyclerView.getParent()).findViewById(R.id.main_mode_switch);
        int removeBtnVisibility = View.INVISIBLE;
        if (removeBtn.isChecked())
            removeBtnVisibility = View.VISIBLE;
        CardDTO newCardDTO = new CardDTO.Builder().rootNo(rootCardNo).containerNo(targetContainerNo).build();
        mCardRepository.insert(newCardDTO.toEntity()
                , orderDropDataInsertListenerForEmptySpace(containerRecyclerView, targetContainerNo, removeBtnVisibility));
    }

    //{@param direction} :
    // viewLocation [left 0, right 1]
    // X coordinate [0, -screenWidth, screenWidth]
    private void slowOut(View view, boolean isReverse, int viewLocation) {
        Logger.message("vm#slowout");
        runOnUiThread(() -> {
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
        }, view.getContext());
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

    private void resetChildrenPresentData(int rootContainerPosition, int rootCardPosition, Integer[] childrenRootNoArr) {
        Logger.message("vm#resetChildrenPresentData");
        Queue<Integer> childrenRootNoQueue = new LinkedList<>();
        Stream.of(childrenRootNoArr).forEach(childrenRootNoQueue::offer);
        final int prevPresentListSize = mPresentData.size();
        int removeBtnVisibility = mPresentData.get(rootContainerPosition).get(rootCardPosition).second.getRemoveBtnVisibility();
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
                presentCardCollector.add(Pair.create(testCard, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
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
        Logger.message("rootNo seq check");
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
    }

    public DropDataInsertListener orderDropDataInsertListenerForEmptySpace(RecyclerView containerRecyclerView, int targetPosition, int removeBtnVisibility) {
        return cardEntity -> {
            mPresentContainerList.add(new Container());
            mPresentData.add(new ArrayList<>());
            CardDTO newCard = cardEntity.toDTO();
            mPresentData.get(targetPosition).add(Pair.create(newCard, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            if (mAllData.size() < targetPosition + 1) {
                mAllData.add(new ArrayList<>());
            }
            mAllData.get(targetPosition).add(newCard);
            mLiveData.postValue(mAllData);
            runOnUiThread(() ->
                    Objects.requireNonNull(containerRecyclerView.getAdapter()).notifyItemInserted(targetPosition), containerRecyclerView.getContext());
        };
    }

    public DropDataInsertListener orderDropDataInsertListenerForContainer(CardDTO targetDTO, CardState targetCardState, List<Pair<CardDTO, CardState>> targetItemList
            , RecyclerView targetRecyclerView) {
        return foundEntity -> {
            Logger.message("DropDataInsertListener#accept");
            int targetSeqNo = targetDTO.getSeqNo();
            int removeBtnVisibility = targetCardState.getRemoveBtnVisibility();
            targetItemList.add(targetSeqNo + 1, Pair.create(foundEntity.toDTO(), new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            mAllData.get(targetDTO.getContainerNo()).add(targetSeqNo + 1, foundEntity.toDTO());
            runOnUiThread(() -> {
                Logger.message("runOnUiThread");
                Objects.requireNonNull(targetRecyclerView.getAdapter()).notifyItemInserted(targetSeqNo + 1);
                targetRecyclerView.scrollToPosition(targetSeqNo + 1);
            }, targetRecyclerView.getContext());
        };
    }

    /* Container Level */

    // +1: For empty card space.
    public int presentContainerCount() {
        Logger.message("vm#presentContainerCount");
        final int EMPTY_CARD_SPACE_COUNT = 1;
        return mPresentData.size() + EMPTY_CARD_SPACE_COUNT;
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

    public int getPresentContainerCount() {
        return mPresentData.size();
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

    // applied to cardView, createFab.
    @BindingAdapter(value = {"onLongClickListener", "card"}, requireAll = false)
    public static void setOnLongClickListener(View view, View.OnLongClickListener listener, @Nullable CardDTO cardDTO) {
        if (Optional.ofNullable(cardDTO).isPresent()) {
            ((CardLongClickListener) listener).setCard(cardDTO);
        }
        view.setOnLongClickListener(listener);
    }

    private static RecyclerView findCardRecyclerViewFromCardView(MaterialCardView cardView) {
        return (RecyclerView) cardView.getParent().getParent().getParent();
    }

//    @BindingAdapter(value = {"onScrollListener", "containerPosition"})
//    public static void setOnScrollListener(View view, RecyclerView.OnScrollListener listener, int containerPosition) {
//        Logger.message("vm#setOnScrollListener for setLm & setContainerPosition container pos :" + containerPosition);
//        RecyclerView rv = (RecyclerView) view;
//        ((CardScrollListener) listener).setLayoutManager((LinearLayoutManager) rv.getLayoutManager());
//        ((CardScrollListener) listener).setContainerPosition(containerPosition);
//        rv.addOnScrollListener(listener);
//    }
//
//    @BindingAdapter("suppressLayout")
//    public static void setSuppressLayout(View view, boolean state) {
//        RecyclerView rv = (RecyclerView) view;
//        rv.suppressLayout(state);
//    }

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
        synchronized (mPresentData) {
            Integer[] childrenRootNoArr = resetPresentContainerList(rootContainerPosition, rootCardPosition);
            resetChildrenPresentContainerRootNo(rootContainerPosition + 1, childrenRootNoArr);
            resetChildrenFocusedCardPosition(rootContainerPosition);
            resetChildrenPresentData(rootContainerPosition, rootCardPosition, childrenRootNoArr);
            notifyContainerItemChanged(((RecyclerView) cardRecyclerView.getParent().getParent()), getContainerAdapterFromCardRecyclerView(cardRecyclerView)
                    , prevPresentContainerSize, mPresentData.size()
                    , rootContainerPosition);
        }
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
            throwToMainHandlerWithDelay(
                    () -> notifyContainerItemChanged(containerRecyclerView, containerAdapter, prevContainerSize, nextContainerSize, rootContainerPosition)
                    , 100
                    , containerRecyclerView.getContext());
            return;
        }
        if (rootContainerPosition + 1 == prevContainerSize && rootContainerPosition + 1 == nextContainerSize)
            return;

        if (prevContainerSize < nextContainerSize) {
            if (prevContainerSize > rootContainerPosition + 1) {
                int changeCount = prevContainerSize - (rootContainerPosition + 1);
                runOnUiThread(() -> containerAdapter.notifyItemRangeChanged(rootContainerPosition + 1, changeCount)
                        , containerRecyclerView.getContext());
            }
            int insertCount = nextContainerSize - prevContainerSize;
            runOnUiThread(() -> containerAdapter.notifyItemRangeInserted(prevContainerSize, insertCount)
                    , containerRecyclerView.getContext());
            return;
        }

        if (prevContainerSize > nextContainerSize) {
            if (prevContainerSize > rootContainerPosition + 1) {
                int changeCount = prevContainerSize - (rootContainerPosition + 1);
                runOnUiThread(() -> containerAdapter.notifyItemRangeChanged(rootContainerPosition + 1, changeCount)
                        , containerRecyclerView.getContext());
            }
            int removeCount = prevContainerSize - nextContainerSize;
            runOnUiThread(() -> containerAdapter.notifyItemRangeRemoved(nextContainerSize, removeCount)
                    , containerRecyclerView.getContext());
            return;
        }

        int changeCount = prevContainerSize - (rootContainerPosition + 1);
        runOnUiThread(() -> containerAdapter.notifyItemRangeChanged(rootContainerPosition + 1, changeCount)
                , containerRecyclerView.getContext());
    }

    public CardScrollListener.OnFocusChangedListener getOnFocusChangedListener() {
        return mOnFocusChangedListener;
    }

    public Container getContainer(int containerPosition) {
        return mPresentContainerList.get(containerPosition);
    }

    public List<Pair<CardDTO, CardState>> getTargetPositionPresentData(int containerPos) {
        return mPresentData.get(containerPos);
    }

    /* TODO : drag card and move card item */
    public View.OnLongClickListener getOnLongClickListenerForCard() {
        return new CardLongClickListener();
    }

    // Utils

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
}