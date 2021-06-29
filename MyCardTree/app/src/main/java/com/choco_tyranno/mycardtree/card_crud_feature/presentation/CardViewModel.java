package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardLongClickListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardScrollListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.Container;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
//    private final MutableLiveData<List<List<CardDTO>>> mLiveData;
    private List<HashMap<Integer, List<CardDTO>>> mAllData;
    private HashMap<Integer, CardDTO> cardIdMap;
    private final List<Container> mPresentContainerList;
    private final List<List<Pair<CardDTO, CardState>>> mPresentData;
    private boolean computingLayout;

    private View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    private View.OnDragListener onDragListenerForCardRecyclerView;
    private View.OnDragListener onDragListenerForVerticalArrow;
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
//        this.mLiveData = new MutableLiveData<>();
        this.mAllData = new ArrayList<>();
        this.mPresentData = new ArrayList<>();
        this.mPresentContainerList = new ArrayList<>();
        this.cardIdMap = new HashMap<>();
        this.computingLayout = false;
        initListeners();
    }

    // ***** Start Listener
    private void initListeners() {
        initCreateCardUtilFabOnLongClickListener();
        initCardRecyclerViewDragListener();
        initEmptyCardSpaceDragListener();
        initOnFocusChangedListener();
        initOnScrollStateChangeListener();
        initVerticalArrowOnDragListener();
    }

    private void initCreateCardUtilFabOnLongClickListener() {
        this.onLongListenerForCreateCardUtilFab
                = (view) -> view.startDragAndDrop(
                ClipData.newPlainText("", "")
                , new CardShadow(view)
                , "CREATE"
                , 0);
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


    //TODO : update target / update existing dropped container items.
    private void dropAndMoveService(@NonNull CardRecyclerView cardRecyclerView) {
        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        final int targetContainerPosition = containerLayoutManager.getCardRecyclerViewPosition(cardRecyclerView);
        LinearLayoutManager cardLayoutManager = cardRecyclerView.getLayoutManager();
        final int firstVisibleCardPosition = cardLayoutManager.findFirstVisibleItemPosition();
        final int lastVisibleCardPosition = cardLayoutManager.findLastVisibleItemPosition();
        //drop to next of firstVisible CardPosition.\
        int rootCardPosition = RecyclerView.NO_POSITION;
        if (targetContainerPosition != 0) {
            rootCardPosition = mPresentContainerList.get(targetContainerPosition - 1).getFocusCardPosition();
            Pair<CardDTO, CardState> rootCardPair = mPresentData.get(targetContainerPosition - 1).get(rootCardPosition);
            CardDTO rootCardDTO = rootCardPair.first;
            CardState rootCardState = rootCardPair.second;

        }

        if (firstVisibleCardPosition == lastVisibleCardPosition) {

        }
    }

    /*
     *
     * The fromData & toData, outer Pair first : containerPosition , second : item.
     * */
    public DropDataUpdateListener<Integer> orderDropDataUpdateListener(int targetContainerPosition, int targetCardPosition
            , RecyclerView targetCardRecyclerView, Queue<Pair<Integer, List<CardDTO>>> fromData
            , Queue<Pair<Integer, List<CardDTO>>> toData) {

        return updateCount -> {
            Logger.hotfixMessage("updateCount:" + updateCount);
            //move data. remove fromData , add toData
            //
            // loop remove
            while (!fromData.isEmpty()) {
                Pair<Integer, List<CardDTO>> dequeueItem = fromData.poll();
                if (dequeueItem == null)
                    return;
                final int theContainerPosition = dequeueItem.first;
                final List<CardDTO> theContainerItems = dequeueItem.second;
                //verify removeAll.
//                mAllData.get(theContainerPosition).removeAll(theContainerItems);
            }
            while (!toData.isEmpty()) {
                Pair<Integer, List<CardDTO>> dequeueItem = toData.poll();
                if (dequeueItem == null)
                    return;
                final int theContainerPosition = dequeueItem.first;
                final List<CardDTO> theContainerItems = dequeueItem.second;
                //verify addAll.
            }


//            targetItemList.add(targetSeqNo + 1, Pair.create(foundEntity.toDTO(), new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
//            mAllData.get(targetDTO.getContainerNo()).add(targetSeqNo + 1, foundEntity.toDTO());
//            runOnUiThread(() -> {
//                Logger.message("runOnUiThread");
//                Objects.requireNonNull(targetRecyclerView.getAdapter()).notifyItemInserted(targetSeqNo + 1);
//                targetRecyclerView.scrollToPosition(targetSeqNo + 1);
//            }, targetRecyclerView.getContext());


            mPresentContainerList.get(targetContainerPosition).setFocusCardPosition(targetCardPosition);
            presentChildren(targetCardRecyclerView, targetContainerPosition, targetCardPosition);
        };
    }

    public interface DropDataUpdateListener<E> {
        void onUpdate(E updateCount);
    }

//    public DropDataInsertListener orderDropDataInsertListenerForContainer(CardDTO targetDTO, CardState targetCardState, List<Pair<CardDTO, CardState>> targetItemList
//            , RecyclerView targetRecyclerView) {
//        return foundEntity -> {
//            Logger.message("DropDataInsertListener#accept");
//            int targetSeqNo = targetDTO.getSeqNo();
//            int removeBtnVisibility = targetCardState.getRemoveBtnVisibility();
//            targetItemList.add(targetSeqNo + 1, Pair.create(foundEntity.toDTO(), new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
//            mAllData.get(targetDTO.getContainerNo()).add(targetSeqNo + 1, foundEntity.toDTO());
//            runOnUiThread(() -> {
//                Logger.message("runOnUiThread");
//                Objects.requireNonNull(targetRecyclerView.getAdapter()).notifyItemInserted(targetSeqNo + 1);
//                targetRecyclerView.scrollToPosition(targetSeqNo + 1);
//            }, targetRecyclerView.getContext());
//        };
//    }

    private void initCardRecyclerViewDragListener() {
        Logger.message("vm#initCardRecyclerViewDragListener");
        onDragListenerForCardRecyclerView = (view, event) -> {
            if (!(view instanceof CardRecyclerView))
                return false;
            CardRecyclerView targetView = (CardRecyclerView) view;
            String dragType = (String) event.getLocalState();

            if (TextUtils.equals(dragType, "CREATE")) {
                return handleCreateServiceForContainer(targetView, event);
            }
            if (TextUtils.equals(dragType, "MOVE")) {
                return handleMoveServiceForCardRecyclerView(targetView, event);
            }
            return false;
        };
    }

    private void initVerticalArrowOnDragListener() {
        this.onDragListenerForVerticalArrow = (view, event) -> {
            final int action = event.getAction();
            if (action == DragEvent.ACTION_DRAG_STARTED) {
                final String dragType = (String) event.getLocalState();
                final boolean moveDragEvent = TextUtils.equals(dragType, "MOVE");
                if (moveDragEvent)
                    return true;
            }

            ContainerRecyclerView containerRecyclerView = ((ViewGroup) view.getParent()).findViewById(R.id.main_body);
            ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
            if (containerLayoutManager == null)
                return false;
            ViewGroup viewGroup = (ViewGroup) containerRecyclerView.getParent();
            View prevContainerArrow = viewGroup.findViewById(R.id.prev_container_arrow);
            View nextContainerArrow = viewGroup.findViewById(R.id.next_container_arrow);

            if (action == DragEvent.ACTION_DRAG_LOCATION) {
                if (containerLayoutManager.hasScrollAction())
                    return false;

                String verticalArrowId = view.getResources().getResourceEntryName(view.getId());

                if (TextUtils.equals(verticalArrowId, "prev_container_arrow")) {
                    final int firstCompletelyVisibleContainerPosition = containerLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstCompletelyVisibleContainerPosition < 0)
                        return false;
                    if (firstCompletelyVisibleContainerPosition != 0) {
                        containerLayoutManager.setContainerScrollAction(() -> {
                            containerRecyclerView.smoothScrollToPosition(firstCompletelyVisibleContainerPosition - 1);
                            if (firstCompletelyVisibleContainerPosition - 1 == 0) {
                                view.setVisibility(View.INVISIBLE);
                            }
                            nextContainerArrow.setVisibility(View.VISIBLE);
                        });
                        containerLayoutManager.scrollDelayed(100);
                        return true;
                    } else
                        return false;
                }

                if (TextUtils.equals(verticalArrowId, "next_container_arrow")) {
                    final int lastCompletelyVisibleContainerPosition = containerLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastCompletelyVisibleContainerPosition < 0)
                        return false;
                    final int containerCount = containerLayoutManager.getItemCount();
                    if (lastCompletelyVisibleContainerPosition != containerCount - 1) {
                        containerLayoutManager.setContainerScrollAction(() -> {
                            containerRecyclerView.smoothScrollToPosition(lastCompletelyVisibleContainerPosition + 1);
                            if (lastCompletelyVisibleContainerPosition + 1 == containerCount - 1) {
                                view.setVisibility(View.INVISIBLE);
                            }
                            prevContainerArrow.setVisibility(View.VISIBLE);
                        });
                        containerLayoutManager.scrollDelayed(100);
                        return true;
                    } else
                        return false;
                }

                return false;
            }

            if (action == DragEvent.ACTION_DRAG_ENTERED) {
                return true;
            }

            if (action == DragEvent.ACTION_DRAG_ENDED) {
                if (containerLayoutManager.hasScrollAction()) {
                    containerLayoutManager.setExitAction(() -> {
                        prevContainerArrow.setVisibility(View.INVISIBLE);
                        nextContainerArrow.setVisibility(View.INVISIBLE);
                    });
                    return true;
                }
                prevContainerArrow.setVisibility(View.INVISIBLE);
                nextContainerArrow.setVisibility(View.INVISIBLE);
                return true;
            }
            return false;
        };
    }


    private void initOnScrollStateChangeListener() {
        mOnScrollStateChangeListener = (savedScrollState, containerPosition) -> {
            if (mPresentContainerList.size() > containerPosition) {
                mPresentContainerList.get(containerPosition).setSavedScrollState(savedScrollState);
            }
        };
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

    public CardScrollListener.OnScrollStateChangeListener getOnScrollStateChangeListener() {
        return mOnScrollStateChangeListener;
    }


    /* remove card */

    public void onRemoveBtnClicked(View view, CardDTO targetCard) {
        int targetContainerPosition = findContainerPositionByRemoveBtn(view);
        List<CardDTO> removeItemList = new ArrayList<>();
        findChildrenCards(targetCard, removeItemList);
        removeItemList.add(targetCard);
        alertDeleteWarningDialog(view, targetCard, removeItemList, targetContainerPosition);
    }

    private void removeFromAllList(CardDTO[] removeItemArr) {
        for (final CardDTO testDto : removeItemArr) {
            final int testRootNo = testDto.getRootNo();
            final int testContainerNo = testDto.getContainerNo();
            mAllData.get(testContainerNo).get(testRootNo).remove(testDto);
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
                    removeFromAllList(removeItemArr);
//                    mLiveData.postValue(mAllData);
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
                    removeFromAllList(removeItemArr);
//                    mLiveData.postValue(mAllData);
                    mPresentData.get(targetContainerPosition).remove(cardDTO.getSeqNo());
                    if (focusedTarget) {
                        runOnUiThread(() -> {
                            RecyclerView targetCardRecyclerView = getCardRecyclerViewFromRemoveButton(view);
                            CardAdapter cardAdapter = (CardAdapter) targetCardRecyclerView.getAdapter();
                            if (cardAdapter == null)
                                return;
                            cardAdapter.notifyItemRemoved(cardDTO.getSeqNo());
                            int newFocusPosition = findNearestItemPosition(targetContainerPosition, cardDTO.getSeqNo());
                            mPresentContainerList.get(targetContainerPosition).setFocusCardPosition(newFocusPosition);
                            presentChildren(targetCardRecyclerView, targetContainerPosition, newFocusPosition);
                            SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "요청한 카드가 삭제되었습니다."));
                        }, view.getContext());
                    } else {
                        runOnUiThread(() -> {
                            CardAdapter cardAdapter = (CardAdapter) getCardRecyclerViewFromRemoveButton(view).getAdapter();
                            if (cardAdapter == null)
                                return;
                            cardAdapter.notifyItemRemoved(cardDTO.getSeqNo());
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

    private void findChildrenCards(CardDTO rootCard, List<CardDTO> foundChildrenCollector) {
        final int testRootNo = rootCard.getCardNo();
        final int testContainerNo = rootCard.getContainerNo() + 1;
        final boolean hasNextContainerData = mAllData.size() > testContainerNo;
        if (!hasNextContainerData) {
            return;
        }
        List<CardDTO> foundCards = mAllData.get(testContainerNo).get(testRootNo);
        if (foundCards == null || foundCards.isEmpty())
            return;
        foundChildrenCollector.addAll(foundCards);
        for (CardDTO foundCard : foundCards) {
            findChildrenCards(foundCard, foundChildrenCollector);
        }
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


    /*
     * Below :
     * Service handling code.
     * */

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

    private boolean handleMoveServiceForCardRecyclerView(CardRecyclerView targetView, DragEvent event) {
        CardAdapter cardAdapter = (CardAdapter) targetView.getAdapter();
        if (cardAdapter == null)
            return false;
        int containerPosition = cardAdapter.getPosition();
        if (containerPosition == -1)
            return false;
        final int screenWidth = DisplayUtil.getScreenWidth(targetView.getContext());
        final int MOVE_BOUNDARY_WIDTH = 200;
        CardRecyclerView.ScrollControllableLayoutManager layoutManager = targetView.getLayoutManager();
        ImageView prevArrow = ((ViewGroup) targetView.getParent()).findViewById(R.id.prev_card_arrow);
        ImageView nextArrow = ((ViewGroup) targetView.getParent()).findViewById(R.id.next_card_arrow);

        if (layoutManager == null)
            return false;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_LOCATION:
                if (layoutManager.hasScrollAction()) {
                    return false;
                }
                synchronized (mPresentData) {
                    final int cardCount = mPresentData.get(containerPosition).size();
                    Container container = mPresentContainerList.get(containerPosition);
                    final int focusedCardPosition = container.getFocusCardPosition();

                    if (event.getX() < MOVE_BOUNDARY_WIDTH && focusedCardPosition != 0) {
                        layoutManager.setScrollAction(() -> {
                            targetView.smoothScrollToPosition(focusedCardPosition - 1);
                            if (focusedCardPosition - 1 == 0)
                                prevArrow.setVisibility(View.INVISIBLE);
                            if (nextArrow.getVisibility() == View.INVISIBLE)
                                nextArrow.setVisibility(View.VISIBLE);
                        });
                        layoutManager.scrollDelayed(500);
                        return true;
                    }

                    if (event.getX() > screenWidth - MOVE_BOUNDARY_WIDTH && focusedCardPosition != cardCount - 1) {
                        layoutManager.setScrollAction(() -> {
                            targetView.smoothScrollToPosition(focusedCardPosition + 1);
                            if (focusedCardPosition + 1 == cardCount - 1)
                                nextArrow.setVisibility(View.INVISIBLE);
                            if (prevArrow.getVisibility() == View.INVISIBLE)
                                prevArrow.setVisibility(View.VISIBLE);
                        });
                        layoutManager.scrollDelayed(500);
                    }
                }

                return true;

            case DragEvent.ACTION_DRAG_STARTED:
                ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) targetView.getParent().getParent();
                LinearLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
                final int firstVisibleContainerPosition = containerLayoutManager.findFirstCompletelyVisibleItemPosition();
                final int lastVisibleContainerPosition = containerLayoutManager.findLastCompletelyVisibleItemPosition();
                final int itemCount = containerLayoutManager.getItemCount();
                ViewGroup viewGroup = (ViewGroup) containerRecyclerView.getParent();
                if (firstVisibleContainerPosition != 0) {
                    viewGroup.findViewById(R.id.prev_container_arrow).setVisibility(View.VISIBLE);
                }
                if (lastVisibleContainerPosition < itemCount - 1) {
                    viewGroup.findViewById(R.id.next_container_arrow).setVisibility(View.VISIBLE);
                }
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (containerPosition > mPresentData.size() - 1)
                    return false;
                synchronized (mPresentData) {
                    final int cardCount = mPresentData.get(containerPosition).size();
                    Container container = mPresentContainerList.get(containerPosition);
                    final int focusedCardPosition = container.getFocusCardPosition();
                    if (focusedCardPosition != 0) {
                        prevArrow.setVisibility(View.VISIBLE);
                    }
                    if (focusedCardPosition < cardCount - 1) {
                        nextArrow.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                if (layoutManager.hasScrollAction()) {
                    layoutManager.setExitAction(() -> {
                        prevArrow.setVisibility(View.INVISIBLE);
                        nextArrow.setVisibility(View.INVISIBLE);
                    });
                    return true;
                }
                prevArrow.setVisibility(View.INVISIBLE);
                nextArrow.setVisibility(View.INVISIBLE);
                return true;
            case DragEvent.ACTION_DROP:
                dropAndMoveService(targetView);
                return true;
        }
        return false;
    }

    private boolean handleCreateServiceForContainer(RecyclerView targetView, DragEvent event) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) targetView.getLayoutManager();
        if (layoutManager == null)
            return false;
        if (event.getAction()==DragEvent.ACTION_DRAG_STARTED)
            return true;
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
        mCardRepository.readData((lastContainerNo) -> {
            initData(lastContainerNo);
            callback.onLoadData();
        });
    }

    private void initPresentData(List<HashMap<Integer, List<CardDTO>>> dataGroupedByRootNo) {
        mPresentData.clear();
        if (!(mPresentContainerList.size() > 0))
            return;
        if (dataGroupedByRootNo == null || dataGroupedByRootNo.isEmpty())
            return;
        for (int i = 0; i < mPresentContainerList.size(); i++) {
            List<Pair<CardDTO, CardState>> foundItemCollector = new ArrayList<>();
            final int testRootNo = mPresentContainerList.get(i).getRootNo();
            List<CardDTO> foundDtoList = dataGroupedByRootNo.get(i).get(testRootNo);
            if (foundDtoList == null || foundDtoList.isEmpty())
                return;
            foundDtoList.forEach(dto -> foundItemCollector.add(Pair.create(dto, new CardState())));
            if (!foundItemCollector.isEmpty())
                mPresentData.add(foundItemCollector);
        }
    }

    private void initData(int lastContainerNo) {
        Logger.message("vm#setData");
        List<CardDTO> allDTOs = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        List<List<CardDTO>> dataGroupedByContainerNo = groupByContainerNo(allDTOs, lastContainerNo + 1);
        List<HashMap<Integer, List<CardDTO>>> dataGroupedByRootNo = groupByRootNo(dataGroupedByContainerNo);
        orderBySeqNo(dataGroupedByRootNo);
        initContainerList(dataGroupedByRootNo);
        initPresentData(dataGroupedByRootNo);
        // for Search func
//        mLiveData.postValue(dataGroupedByContainerNo);
        // TODO : check [following 2 line] is it redundant?
        mAllData.clear();
        mAllData.addAll(dataGroupedByRootNo);
    }

    private void initContainerList(List<HashMap<Integer, List<CardDTO>>> orderedData) {
        if (orderedData == null || orderedData.isEmpty())
            return;
        List<CardDTO> firstLayerItems = orderedData.get(0).get(CardDTO.NO_ROOT_CARD);
        if (firstLayerItems == null || firstLayerItems.isEmpty())
            return;
        mPresentContainerList.add(new Container(CardDTO.NO_ROOT_CARD));
        int rootCardNo = firstLayerItems.get(Container.DEFAULT_CARD_POSITION).getCardNo();
        for (int i = 1; i < orderedData.size(); i++) {
            HashMap<Integer, List<CardDTO>> testMap = orderedData.get(i);
            if (testMap.containsKey(rootCardNo)) {
                List<CardDTO> testList = testMap.get(rootCardNo);
                if (testList == null || testList.isEmpty())
                    return;
                mPresentContainerList.add(new Container(rootCardNo));
                rootCardNo = testList.get(0).getCardNo();
                continue;
            }
            break;
        }
    }

    private void orderBySeqNo(List<HashMap<Integer, List<CardDTO>>> groupedData) {
        for (HashMap<Integer, List<CardDTO>> testContainerMap : groupedData) {
            for (int key : testContainerMap.keySet()) {
                List<CardDTO> testDtoList = testContainerMap.get(key);
                if (testDtoList != null)
                    Collections.sort(testDtoList);
            }
        }
    }

    private List<HashMap<Integer, List<CardDTO>>> groupByRootNo(List<List<CardDTO>> dataGroupedByContainerNo) {
        List<HashMap<Integer, List<CardDTO>>> result = new ArrayList<>();
        if (dataGroupedByContainerNo == null || dataGroupedByContainerNo.isEmpty())
            return result;
        for (int i = 0; i < dataGroupedByContainerNo.size(); i++) {
            result.add(new HashMap<>());
        }
        final int containerCount = dataGroupedByContainerNo.size();
        for (int i = 0; i < containerCount; i++) {
            List<CardDTO> testContainerCardDtoList = dataGroupedByContainerNo.get(i);
            for (CardDTO testCardDto : testContainerCardDtoList) {
                final int testRootNo = testCardDto.getRootNo();
                final HashMap<Integer, List<CardDTO>> testMap = result.get(i);
                if (!testMap.containsKey(testRootNo))
                    testMap.put(testRootNo, new ArrayList<>());
                Objects.requireNonNull(testMap.get(testRootNo)).add(testCardDto);
            }
//            for (int key : result.get(i).keySet()){
//                Logger.hotfixMessage("["+i+"] key :"+key+"/ size:"+result.get(i).get(key).size());
//            }
        }
        return result;
    }

    private List<List<CardDTO>> groupByContainerNo(List<CardDTO> data, int containerSize) {
        Logger.message("vm#groupDataByContainerNo");
        List<List<CardDTO>> result = new ArrayList<>();
        if (data == null || data.isEmpty())
            return result;
        for (int i = 0; i < containerSize; i++) {
            result.add(new ArrayList<>());
        }
        for (CardDTO testDto : data) {
            final int containerPosition = testDto.getContainerNo();
            result.get(containerPosition).add(testDto);
        }
        return result;
    }

    private void resetChildrenPresentData(int rootContainerPosition, int rootCardPosition) {
        Logger.message("vm#resetChildrenPresentData");
        Pair<CardDTO, CardState> rootCardPair = mPresentData.get(rootContainerPosition).get(rootCardPosition);
        CardDTO rootCardDto = rootCardPair.first;
        CardState rootCardState = rootCardPair.second;
        final int removeBtnVisibility = rootCardState.getRemoveBtnVisibility();
        final int prevPresentListSize = mPresentData.size();
        final boolean hasUselessContainer = prevPresentListSize > rootContainerPosition + 1;
        if (hasUselessContainer) {
            mPresentData.subList(rootContainerPosition + 1, prevPresentListSize).clear();
        }

        boolean hasNextContainerData = mAllData.size() > rootContainerPosition + 1;
        while (hasNextContainerData) {
            final int rootCardNo = rootCardDto.getCardNo();
            HashMap<Integer, List<CardDTO>> testMap = mAllData.get(rootContainerPosition + 1);
            if (testMap == null || testMap.isEmpty())
                break;
            if (!testMap.containsKey(rootCardNo)) {
                break;
            }
            List<CardDTO> foundList = testMap.get(rootCardNo);
            if (foundList == null || foundList.isEmpty())
                break;
            mPresentData.add(
                    dtoListToPairList(foundList, removeBtnVisibility)
            );
            rootContainerPosition++;
            hasNextContainerData = mAllData.size() > rootContainerPosition + 1;
            rootCardDto = foundList.get(Container.DEFAULT_CARD_POSITION);
        }
    }

    private List<Pair<CardDTO, CardState>> dtoListToPairList(List<CardDTO> dtoList) {
        List<Pair<CardDTO, CardState>> result = new ArrayList<>();
        if (dtoList == null || dtoList.isEmpty())
            return result;
        for (CardDTO dto : dtoList) {
            result.add(Pair.create(dto, new CardState.Builder().build()));
        }
        return result;
    }

    private List<Pair<CardDTO, CardState>> dtoListToPairList(List<CardDTO> dtoList, int removeBtnVisibility) {
        List<Pair<CardDTO, CardState>> result = new ArrayList<>();
        if (dtoList == null || dtoList.isEmpty())
            return result;
        for (CardDTO dto : dtoList) {
            result.add(Pair.create(dto, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
        }
        return result;
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

    //TODO : mAllData ->mAllData.
    public DropDataInsertListener orderDropDataInsertListenerForEmptySpace(RecyclerView containerRecyclerView, int targetPosition, int removeBtnVisibility) {
        return cardEntity -> {
            mPresentContainerList.add(new Container());
            mPresentData.add(new ArrayList<>());
            CardDTO newCard = cardEntity.toDTO();
            mPresentData.get(targetPosition).add(Pair.create(newCard, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            final boolean noValueInAllData = mAllData.size() < targetPosition + 1;
            if (noValueInAllData) {
                HashMap<Integer, List<CardDTO>> dtoListMap = new HashMap<>();
                mAllData.add(dtoListMap);
                List<CardDTO> newDtoList = new ArrayList<>();
                newDtoList.add(newCard);
                dtoListMap.put(newCard.getRootNo(), newDtoList);
            }
//            mLiveData.postValue(mAllData);
            runOnUiThread(() ->
                    Objects.requireNonNull(containerRecyclerView.getAdapter()).notifyItemInserted(targetPosition), containerRecyclerView.getContext());
        };
    }

    public DropDataInsertListener orderDropDataInsertListenerForContainer(CardDTO targetDTO, CardState targetCardState, List<Pair<CardDTO, CardState>> targetItemList
            , RecyclerView targetRecyclerView) {
        return foundEntity -> {
            Logger.message("DropDataInsertListener#accept");
            final int targetSeqNo = targetDTO.getSeqNo();
            final int newCardSeqNo = targetSeqNo + 1;
            final int removeBtnVisibility = targetCardState.getRemoveBtnVisibility();
            targetItemList.add(newCardSeqNo, Pair.create(foundEntity.toDTO(), new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            final int targetContainerNo = targetDTO.getContainerNo();

            //TODO : verify this.
            mAllData.get(targetContainerNo).get(foundEntity.getRootNo()).add(newCardSeqNo, foundEntity.toDTO());
            runOnUiThread(() -> {
                Logger.message("runOnUiThread");
                Objects.requireNonNull(targetRecyclerView.getAdapter()).notifyItemInserted(newCardSeqNo);
                targetRecyclerView.scrollToPosition(newCardSeqNo);
                mPresentContainerList.get(targetContainerNo).setFocusCardPosition(newCardSeqNo);
                presentChildren(targetRecyclerView, targetContainerNo, newCardSeqNo);
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

    public View.OnDragListener getOnDragListenerForVerticalArrow() {
        return onDragListenerForVerticalArrow;
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

    /* TODO : this*/
    public void presentChildren(RecyclerView cardRecyclerView, int rootContainerPosition, int rootCardPosition) {
        Logger.message("vm#presentChildren/ rootContainerPosition :" + rootContainerPosition + "/rootCardPosition" + rootCardPosition);
        synchronized (mPresentData) {
            final int prevPresentContainerSize = mPresentContainerList.size();
            resetPresentContainerList(rootContainerPosition, rootCardPosition);
            resetChildrenPresentData(rootContainerPosition, rootCardPosition);
            notifyContainerItemChanged(((RecyclerView) cardRecyclerView.getParent().getParent()), getContainerAdapterFromCardRecyclerView(cardRecyclerView)
                    , prevPresentContainerSize, mPresentData.size()
                    , rootContainerPosition);
        }
    }


    private void resetPresentContainerList(int rootContainerPosition, int rootCardPosition) {
        final int prevLastPosition = mPresentContainerList.size() - 1;
        if (prevLastPosition > rootContainerPosition) {
            mPresentContainerList.subList(rootContainerPosition + 1, prevLastPosition + 1).clear();
        }

        boolean hasNextContainer = mAllData.size() > rootContainerPosition + 1;
        CardDTO rootCard = mPresentData.get(rootContainerPosition).get(rootCardPosition).first;
        while (hasNextContainer) {
            final int rootCardNo = rootCard.getCardNo();
            List<CardDTO> testList = mAllData.get(rootContainerPosition + 1).get(rootCardNo);
            if (testList == null || testList.isEmpty())
                return;
            mPresentContainerList.add(new Container(rootCardNo));
            rootContainerPosition++;
            hasNextContainer = mAllData.size() > rootContainerPosition + 1;
            rootCard = testList.get(0);
        }
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