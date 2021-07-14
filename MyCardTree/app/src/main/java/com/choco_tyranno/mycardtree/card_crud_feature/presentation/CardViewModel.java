package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
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
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardGestureListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardLongClickListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardScrollListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardTouchListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ImageToFullScreenClickListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ObservableBitmap;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.Container;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CardViewModel extends AndroidViewModel implements UiThreadAccessible {
    private final CardRepository mCardRepository;
    //    private final MutableLiveData<List<List<CardDTO>>> mLiveData;
    private List<HashMap<Integer, List<CardDTO>>> mAllData;
    private HashMap<Integer, ObservableBitmap> cardImageMap;
    private List<Container> mPresentContainerList;
    private List<List<Pair<CardDTO, CardState>>> mPresentData;

    private View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    private View.OnDragListener onDragListenerForCardRecyclerView;
    private View.OnDragListener onDragListenerForVerticalArrow;
    private View.OnDragListener onDragListenerForEmptyCardSpace;
    private View.OnClickListener onClickListenerForImageViewToFullScreen;
    private CardScrollListener.OnFocusChangedListener mOnFocusChangedListener;
    private CardScrollListener.OnScrollStateChangeListener mOnScrollStateChangeListener;

    private CardTouchListener cardTouchListener;
    private GestureDetectorCompat cardGestureDetector;

    private CardGestureListener cardGestureListener;

    private final int CARD_LOCATION_LEFT = 0;
    private final int CARD_LOCATION_RIGHT = 1;

//    TODO : [latest], [now work]

    public int getRepositoryDataSize(){
        return mCardRepository.getData().size();
    }

    public void addCardImageValue(CardDTO cardDTO){
        cardImageMap.put(cardDTO.getCardNo(), new ObservableBitmap());
    }

    public boolean hasDefaultCardImage() {
        return cardImageMap.containsKey(CardDTO.NO_ROOT_CARD);
    }

    public Bitmap getDefaultCardThumbnail() {
        return Objects.requireNonNull(cardImageMap.get(CardDTO.NO_ROOT_CARD)).getThumbnail();
    }

    @BindingAdapter(value = {"cardThumbnail", "defaultCardThumbnail"})
    public static void loadCardThumbnail(ImageView view, Bitmap cardThumbnail, Bitmap defaultCardThumbnail) {
        if (cardThumbnail == null)
            view.setImageBitmap(defaultCardThumbnail);
        else
            view.setImageBitmap(cardThumbnail);

//    @BindingAdapter(value = {"viewModel","cardNo"}, requireAll = true)
//    public static void loadCardImage(ImageView view, CardViewModel viewModel,int cardNo){
//        Logger.hotfixMessage("loadCardImage");
//        view.setImageBitmap(viewModel.getCardImage(cardNo));

//        int width = Math.round(view.getResources().getDimension(R.dimen.card_thumbnail_image_width));
//        int height = Math.round(view.getResources().getDimension(R.dimen.card_thumbnail_image_height));
//        Glide.with(view.getContext()).asBitmap()
//                .load(url)
//                .placeholder(R.drawable.default_card_image_01)
//                .override(width,height)
//                .into(view);
    }

    public void setDefaultCardImage(Bitmap defaultCardImage) {
        cardImageMap.put(CardDTO.NO_ROOT_CARD, new ObservableBitmap(defaultCardImage));
    }


    public void connectGestureUtilsToOnCardTouchListener() {
        if (cardTouchListener != null && cardGestureDetector != null && cardGestureListener != null) {
            cardTouchListener.setCardGestureDetectorCompat(cardGestureDetector);
            cardTouchListener.setCardGestureListener(cardGestureListener);
        }
    }

    public void setCardGestureListener(CardGestureListener listener) {
        this.cardGestureListener = listener;
    }

    public void setCardGestureDetector(GestureDetectorCompat cardGestureDetector) {
        this.cardGestureDetector = cardGestureDetector;
    }

    public View.OnTouchListener getOnTouchListener() {
        return cardTouchListener;
    }

    public CardGestureListener getCardGestureListener() {
        return cardGestureListener;
    }

    public GestureDetectorCompat getCardGestureDetector() {
        return cardGestureDetector;
    }

    @BindingAdapter("onCardTouchListener")
    public static void setOnCardTouchListener(View view, View.OnTouchListener touchListener) {
        view.setOnTouchListener(touchListener);
    }

    @BindingAdapter("onClickListener")
    public static void setOnClickListener(View view, View.OnClickListener clickListener){
        view.setOnClickListener(clickListener);
    }

    public View.OnClickListener getOnImageViewToFullscreenClickListener(){
        return onClickListenerForImageViewToFullScreen;
    }

    /*
     * DO NEXT : flip card.
     *
     * */

    public void printTargetCard(int containerNo, int cardSeqNo) {
        CardState cardState = mPresentData.get(containerNo).get(cardSeqNo).second;
        boolean flipped = cardState.isFlipped();
        CardState.Front front = cardState.getFront();
        CardState.Back back = cardState.getBack();
        Logger.hotfixMessage("front - alpha: " + front.getCardFrontAlpha()
                + "/ visibility : " + front.getCardFrontVisibility()
                + "/rotationX : " + front.getCardFrontRotationX());

        Logger.hotfixMessage("back - alpha: " + back.getCardBackAlpha()
                + "/ visibility : " + back.getCardBackVisibility()
                + "/rotationX : " + back.getCardBackRotationX());
        Logger.hotfixMessage("flipped" + flipped);
    }

    public void printContainers() {
        Logger.hotfixMessage("#printContainers() start");
        int i = 0;
        for (Container container : mPresentContainerList) {
            Logger.hotfixMessage("container [" + i + "] - /focusPos :" + container.getFocusCardPosition()
                    + "/rootNo:" + container.getRootNo());
            i++;
        }
        Logger.hotfixMessage("#printContainers() end");
    }

    public void printAllData() {
        Logger.hotfixMessage("#printAllData() start");
        for (int i = 0; i < mAllData.size(); i++) {
            Logger.hotfixMessage("mAllData.get(" + i + ")");
            HashMap<Integer, List<CardDTO>> testMap = mAllData.get(i);
            String testMapExistMsg = "/testMapExist : ";
            testMapExistMsg = testMapExistMsg.concat(testMap == null ? "null" : "exist");
            Logger.hotfixMessage(testMapExistMsg);
            String keySetMsg = "/keySet : ";
            if (testMap != null) {
                if (testMap.keySet().isEmpty()) {
                    keySetMsg = keySetMsg.concat("Empty");
                    Logger.hotfixMessage(keySetMsg);
                } else {
                    for (Integer key : testMap.keySet()) {
                        keySetMsg = keySetMsg.concat(key.toString()).concat(" ");
                    }
                    Logger.hotfixMessage(keySetMsg);

                    for (Integer key : testMap.keySet()) {
                        List<CardDTO> testList = testMap.get(key);
                        if (testList == null)
                            Logger.hotfixMessage("key[" + key + "]" + " value(list) is null");
                        else {
                            if (testList.isEmpty())
                                Logger.hotfixMessage("key[" + key + "]" + " value(list) is empty");
                            else {
                                for (CardDTO testCard : testList)
                                    Logger.hotfixMessage("key[" + key + "] List item - seqNo:" + testCard.getSeqNo()
                                            + "/cardNo:" + testCard.getCardNo() + "/rootNo:" + testCard.getRootNo()
                                            + "/containerNo:" + testCard.getContainerNo()
                                            + "/title:" + testCard.getTitle());
                            }
                        }
                    }
                }
            }
        }
        Logger.hotfixMessage("#printAllData() end");
    }


    /* Default constructor*/

    public CardViewModel(Application application) {
        super(application);
        Logger.hotfixMessage("VM#constructor");
        Logger.message("VM#constructor");
        this.mCardRepository = new CardRepository(application);
//        this.mLiveData = new MutableLiveData<>();
        this.mAllData = new ArrayList<>();
        this.mPresentData = new ArrayList<>();
        this.mPresentContainerList = new ArrayList<>();
        this.cardImageMap = new HashMap<>();
        initListeners();
    }

    // ***** Start Listener
    private void initListeners() {
        initCreateCardUtilFabOnLongClickListener();
        initCardRecyclerViewDragListener();
        initEmptyCardSpaceDragListener();
        initVerticalArrowOnDragListener();
        initOnFocusChangedListener();
        initOnScrollStateChangeListener();
        initCardTouchListener();
        initImageToFullScreenClickListener();
    }

    private void initImageToFullScreenClickListener() {
        onClickListenerForImageViewToFullScreen = new ImageToFullScreenClickListener();
    }

    private void initCardTouchListener() {
        cardTouchListener = new CardTouchListener();
    }


    private void initCreateCardUtilFabOnLongClickListener() {
        this.onLongListenerForCreateCardUtilFab
                = (view) -> view.startDragAndDrop(
                ClipData.newPlainText("", "")
                , new CardShadow(view)
                , Pair.create("CREATE", "")
                , 0);
    }

    //Drop target view is recyclerView.
    //single card case + in crowds case.
    private void dropAndCreateServiceForContainer(CardRecyclerView cardRecyclerView, FrameLayout prevSeqCardView) {
        Logger.message("vm#dropAndCreateService : for card space");
        if (cardRecyclerView == null || prevSeqCardView == null) {
            return;
        }
        ItemCardFrameBinding cardFrameBinding = ((ContactCardViewHolder) cardRecyclerView.getChildViewHolder(prevSeqCardView)).getBinding();
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
                    , orderDropDataInsertListenerForContainer(prevSeqCardDTO, prevCardState, targetContainerCardList, cardRecyclerView)
            );
        } else {
            mCardRepository.insert(newCardDTO.toEntity()
                    , orderDropDataInsertListenerForContainer(prevSeqCardDTO, prevCardState, targetContainerCardList, cardRecyclerView)
            );
        }
    }

    public void increaseListSeq(List<CardDTO> list) {
        if (list == null || list.isEmpty())
            return;
        for (CardDTO cardDTO : list) {
            cardDTO.setSeqNo(cardDTO.getSeqNo() + 1);
        }
    }

    public synchronized void reduceListSeq(List<CardDTO> list) {
        if (list == null || list.isEmpty())
            return;
        for (CardDTO cardDTO : list) {
            cardDTO.setSeqNo(cardDTO.getSeqNo() - 1);
        }
    }


    //TODO : [latest] / [now work]
    private void onMovingCardDroppedInEmptySpace(ContainerRecyclerView containerRecyclerView, DragEvent event) {
        Pair<String, Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>>> nestedDataPair = (Pair<String, Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>>>) event.getLocalState();
        Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>> savedDataPair = nestedDataPair.second;
        CardDTO movingRootCard = savedDataPair.first;
        Pair<List<CardDTO>, List<CardDTO>> movingCardsAndNextCards = savedDataPair.second;
        List<CardDTO> movingCards = movingCardsAndNextCards.first;
        List<CardDTO> pastNextCards = movingCardsAndNextCards.second;
        /*
         * rootCard.setSeqNo, .setRootNo, setContainerNo
         * movingCards.setContainerNo
         * addToAllData(movingCards)
         *
         * addSinglePResentCardDto(rootCard)
         * addContainer(targetContainerNo, rootNo)
         * containerAdapter.notifyItemInserted(targetContainerPosition);
         * delay 150, post -> found targetContainerCardRv and presentChildren
         * */
        if (containerRecyclerView == null)
            return;

        LinearLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        if (containerLayoutManager == null)
            return;
        final int containerRecyclerViewItemCount = containerLayoutManager.getItemCount();
        final int targetContainerPosition = containerRecyclerViewItemCount - 1;
        movingRootCard.setSeqNo(Container.DEFAULT_CARD_POSITION);

        Runnable uiUpdateAction = () -> {
            addToAllData(movingCards.toArray(new CardDTO[0]));
            addSinglePresentCardDto(movingRootCard);
            addContainer(targetContainerPosition, movingRootCard.getRootNo());
            containerRecyclerView.getAdapter();
            if (!(containerRecyclerView.getAdapter() instanceof ContainerAdapter))
                return;
            ContainerAdapter containerAdapter = (ContainerAdapter) containerRecyclerView.getAdapter();

            runOnUiThread(() -> {
                containerAdapter.notifyItemInserted(targetContainerPosition);
                throwToMainHandlerWithDelay(() -> {
                    containerRecyclerView.smoothScrollToPosition(targetContainerPosition);
                    throwToMainHandlerWithDelay(() -> {
                        CardRecyclerView targetContainerCardRecyclerView = findCardRecyclerViewFromContainerRecyclerView(containerRecyclerView, targetContainerPosition);
                        if (targetContainerCardRecyclerView == null)
                            return;
                        presentChildren(targetContainerCardRecyclerView, targetContainerPosition, movingRootCard.getSeqNo());
                    }, 250, containerRecyclerView.getContext());
                }, 150, containerRecyclerView.getContext());
            }, containerRecyclerView.getContext());
        };

        List<CardDTO> dataToUpdate = new ArrayList<>();
        dataToUpdate.addAll(movingCards);
        dataToUpdate.addAll(pastNextCards);
        movingRootCard.setContainerNo(targetContainerPosition);
        if (targetContainerPosition == 0) {
            movingRootCard.setRootNo(CardDTO.NO_ROOT_CARD);
            mCardRepository.update(dtoListToEntityList(dataToUpdate), uiUpdateAction);
//            throwToMainHandlerWithDelay(uiUpdateAction, 150, containerRecyclerView.getContext());
        } else {
            final int aboveContainerPosition = targetContainerPosition - 1;
            containerRecyclerView.smoothScrollToPosition(aboveContainerPosition);
            Runnable findAboveContainerAndSetRootNoAction = () -> {
                if (mPresentContainerList.size() > aboveContainerPosition) {
                    Container aboveContainer = mPresentContainerList.get(aboveContainerPosition);
                    final int aboveContainerFocusCardPosition = aboveContainer.getFocusCardPosition();
                    final int aboveFocusCardNo = mPresentData.get(aboveContainerPosition).get(aboveContainerFocusCardPosition).first.getCardNo();
                    movingRootCard.setRootNo(aboveFocusCardNo);
                    mCardRepository.update(dtoListToEntityList(dataToUpdate), uiUpdateAction);
//                    throwToMainHandlerWithDelay(uiUpdateAction, 150, containerRecyclerView.getContext());
                }
            };
            throwToMainHandlerWithDelay(findAboveContainerAndSetRootNoAction, 250, containerRecyclerView.getContext());
        }
    }

    private void onMovingCardDroppedInContainer(@NonNull CardRecyclerView targetContainerCardRecyclerView, DragEvent event) {
        Pair<String, Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>>> nestedDataPair = (Pair<String, Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>>>) event.getLocalState();
        Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>> savedDataPair = nestedDataPair.second;
        CardDTO movingRootCard = savedDataPair.first;
        Pair<List<CardDTO>, List<CardDTO>> movingCardsAndNextCards = savedDataPair.second;
        List<CardDTO> movingCards = movingCardsAndNextCards.first;
        List<CardDTO> pastNextCards = movingCardsAndNextCards.second;
        CardAdapter targetContainerCardAdapter = targetContainerCardRecyclerView.getAdapter();
        if (targetContainerCardAdapter == null)
            return;
        final int targetContainerPosition = targetContainerCardAdapter.getPosition();
        if (targetContainerPosition < 0)
            return;
        //movingCards contains rootCard.
        //nextCards can be empty.

        //prepare update.
        //-find target container nextCards & increase seq.
        //-set seqNo/rootNo for movingRootCard.
        //-set containerNo for movingCards.
        //->
        if (mPresentContainerList.size() < targetContainerPosition + 1)
            return;
        Container targetContainer = mPresentContainerList.get(targetContainerPosition);
        if (targetContainer == null)
            return;
        final int targetContainerFocusCardPosition = targetContainer.getFocusCardPosition();
        List<CardDTO> targetContainerNextCards = new ArrayList<>();
        findNextCards(targetContainerPosition, targetContainerFocusCardPosition - 1, targetContainerNextCards);
        increaseListSeq(targetContainerNextCards);
        int targetContainerRootNo = targetContainer.getRootNo();
        //TODO : this
        if (targetContainerRootNo == Container.NO_ROOT_NO)
            throw new RuntimeException("[now work]targetContainerRootNo == Container.NO_ROOT_NO / #onMovingCardDroppedInContainer /");
        movingRootCard.setRootNo(targetContainer.getRootNo());
        movingRootCard.setSeqNo(targetContainerFocusCardPosition);
        final int adjustContainerNoCount = targetContainerPosition - movingRootCard.getContainerNo();
        adjustListContainerNo(movingCards, adjustContainerNoCount);
        //fin

        //updateData
        List<CardDTO> dataToUpdate = new ArrayList<>();
        dataToUpdate.addAll(movingCards);
        dataToUpdate.addAll(pastNextCards);
        dataToUpdate.addAll(targetContainerNextCards);
        //movingCards, nextCards, targetContainerNextCards.
        //->
        Runnable uiUpdateAction = () -> {
            //add movingCards into AllData
            addToAllData(movingCards.toArray(new CardDTO[0]));

            //update UI
            //(later) animate blow away kicked out card && after notifyInserted, animate return.
            //
            //add movingRootCard into mPresentData && target container CardRecyclerView.notifyItemInserted.
            //setFocusCardPosition to target container. && {later} smoothScrollToPosition(movingRootCard.getSeqNo) && presentChildren().
            addSinglePresentCardDto(movingRootCard);
            runOnUiThread(() -> targetContainerCardRecyclerView.getAdapter().notifyItemInserted(movingRootCard.getSeqNo())
                    , targetContainerCardRecyclerView.getContext());
            throwToMainHandlerWithDelay(() -> {
                targetContainerCardRecyclerView.smoothScrollToPosition(movingRootCard.getSeqNo());
                presentChildren(targetContainerCardRecyclerView, movingRootCard.getContainerNo(), movingRootCard.getSeqNo());
            }, 150, targetContainerCardRecyclerView.getContext());
        };
        mCardRepository.update(dtoListToEntityList(dataToUpdate), uiUpdateAction);
    }

    private void adjustListContainerNo(List<CardDTO> cards, int adjustCount) {
        if (cards == null)
            return;
        for (CardDTO card : cards) {
            card.setContainerNo(card.getContainerNo() + adjustCount);
        }
    }

    //refactor after item detaching execution.
    private void dropAndMoveServiceForContainer(@NonNull CardRecyclerView cardRecyclerView, DragEvent event) {
        Pair<String, Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>>> pair1 = (Pair<String, Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>>>) event.getLocalState();
        Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>> savedOriginData = pair1.second;
        CardDTO movingRootCard = savedOriginData.first;
        Pair<List<CardDTO>, List<CardDTO>> movingCardsAndNextCards = savedOriginData.second;
        List<CardDTO> movingCards = movingCardsAndNextCards.first;
        List<CardDTO> nextCards = movingCardsAndNextCards.second;
//        CardDTO.orderByContainerNoDesc(movingCards);
        //sort?
//        Logger.hotfixMessage("movingCards size:"+movingCards.size());
//        for (CardDTO dto : movingCards){
//            Logger.hotfixMessage("dto :"+dto.getTitle());
//        }


        /*
//        CardDTO movingCard = (CardDTO) savedOriginData.first;
//        CardDTO[] roll
        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        final int targetContainerPosition = containerLayoutManager.getCardRecyclerViewPosition(cardRecyclerView);
        LinearLayoutManager cardLayoutManager = cardRecyclerView.getLayoutManager();

        // -> delegate to focusPosition
//        final int firstVisibleCardPosition = cardLayoutManager.findFirstVisibleItemPosition();
//        final int lastVisibleCardPosition = cardLayoutManager.findLastVisibleItemPosition();
        //drop to next of firstVisible CardPosition.
        int targetContainerRootCardNo = CardDTO.NO_ROOT_CARD;
        final int removeBtnVisibility = (findRemoveBtnByContainerRecyclerView((RecyclerView) cardRecyclerView.getParent().getParent())).getVisibility();
        if (targetContainerPosition != 0) {
            targetContainerRootCardNo = mPresentContainerList.get(targetContainerPosition - 1).getRootNo();
//            targetContainerRootCardNo = mPresentData.get(targetContainerPosition - 1)
//                    .get(mPresentContainerList.get(targetContainerPosition - 1).getFocusCardPosition())
//                    .first.getCardNo();
        }
//        final int containerNoAdjustingCount = targetContainerPosition - movingCard.getContainerNo();
        final int focusCardPosition = mPresentContainerList.get(targetContainerPosition).getFocusCardPosition();

        *//* --- *//*
         *//* --- *//*

//        movingCard.setRootNo(targetContainerRootCardNo);
//        movingCard.setContainerNo(targetContainerPosition);
//        movingCard.setSeqNo(focusCardPosition + 1);

//        if (!foundNextCards.isEmpty()) {
//            increaseListSeq(foundNextCards);
//        }

        //increase found items.
*//*        mCardRepository.update(
                dtoListToEntityList(moveItemList)
                , () -> {
                    //remove from presentData / mAllData.
                    //add to mAllData.
                    moveItemList.removeAll(foundNextCards);
                    addToAllData((CardDTO[]) moveItemList.toArray());
                    //notify inserted.
                    //scroll to position

                    Objects.requireNonNull(cardRecyclerView.getAdapter()).notifyItemInserted(movingCard.getSeqNo());
                    cardRecyclerView.scrollToPosition(movingCard.getSeqNo());
                    mPresentContainerList.get(targetContainerPosition).setFocusCardPosition(movingCard.getSeqNo());
                    presentChildren(cardRecyclerView, targetContainerPosition, movingCard.getSeqNo());
                });*//*

//        if (targetContainerCardList.size() > prevCardSeqNo + 1) {
//            mCardRepository.insertAndUpdates(
//                    newCardDTO.toEntity()
//                    , dtoListToEntityList(
//                            increaseListCardsSeq(targetContainerCardList, prevCardSeqNo + 1))
//                    , orderDropDataInsertListenerForContainer(prevSeqCardDTO, prevCardState, targetContainerCardList, rv)
//            );
//        } else {
//            mCardRepository.insert(newCardDTO.toEntity()
//                    , orderDropDataInsertListenerForContainer(prevSeqCardDTO, prevCardState, targetContainerCardList, rv)
//            );
//        }*/

    }

    private void addToAllData(CardDTO[] cardArr) {
        if (cardArr == null || cardArr.length == 0)
            return;
        Arrays.sort(cardArr, (o1, o2) -> Integer.compare(o1.getContainerNo(), o2.getContainerNo()));
        final int lastContainerPosition = cardArr[cardArr.length - 1].getContainerNo();
        if (mAllData.size() < lastContainerPosition + 1) {
            while (mAllData.size() < lastContainerPosition + 1) {
                mAllData.add(new HashMap<>());
            }
        }
        for (CardDTO cardDTO : cardArr) {
            final int targetContainerPosition = cardDTO.getContainerNo();
            final int targetRootNo = cardDTO.getRootNo();
            final HashMap<Integer, List<CardDTO>> targetMap = mAllData.get(targetContainerPosition);

            if (!targetMap.containsKey(targetRootNo)) {
                targetMap.put(targetRootNo, new ArrayList<>());
            }
            List<CardDTO> targetList = targetMap.get(targetRootNo);
            Objects.requireNonNull(targetList).add(cardDTO);
            Collections.sort(targetList);
        }
    }

    /*
     * @param flagCardPosition is exclude position.
     * */
    public void findNextCards(int containerPosition, int lastExceptCardPosition, List<CardDTO> foundItemCollector) {
        if (mPresentData.size() < containerPosition + 1)
            return;
        List<Pair<CardDTO, CardState>> targetContainerCards = mPresentData.get(containerPosition);
        if (targetContainerCards.isEmpty())
            return;
        if (targetContainerCards.size() > lastExceptCardPosition + 1) {
            List<Pair<CardDTO, CardState>> filteredSubList = targetContainerCards.subList(lastExceptCardPosition + 1, targetContainerCards.size());
            pairListToCardDtoList(filteredSubList);
            foundItemCollector.addAll(pairListToCardDtoList(filteredSubList));
        }
    }

    /*
     * return :  Has item after item removed from mPresentData.
     * */
    public synchronized boolean removeSinglePresentCardDto(CardDTO cardDTO) {
        final int containerNo = cardDTO.getContainerNo();
        final int seqNo = cardDTO.getSeqNo();
        mPresentData.get(containerNo).remove(seqNo);
        return !mPresentData.get(containerNo).isEmpty();
    }

    private void addSinglePresentCardDto(CardDTO cardDTO) {
        final int containerNo = cardDTO.getContainerNo();
        final int seqNo = cardDTO.getSeqNo();
        if (mPresentData.size() < containerNo + 1) {
            mPresentData.add(new ArrayList<>());
        }
        mPresentData.get(containerNo).add(seqNo, Pair.create(cardDTO, new CardState()));
    }

    public synchronized void clearContainerPositionPresentData(int startContainerPosition) {
        mPresentData.subList(startContainerPosition, mPresentData.size()).clear();
    }

    private List<CardDTO> pairListToCardDtoList(List<Pair<CardDTO, CardState>> pairList) {
        return pairList.stream().map(pair -> pair.first).collect(Collectors.toList());
    }


    /*
     *
     * The fromData & toData, outer Pair first : containerPosition , second : item.
     * */
    public DropDataUpdateListener<Integer> orderDropDataUpdateListener(int targetContainerPosition, int targetCardPosition
            , RecyclerView targetCardRecyclerView, Queue<Pair<Integer, List<CardDTO>>> fromData
            , Queue<Pair<Integer, List<CardDTO>>> toData) {

        return updateCount -> {
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

    public synchronized void clearContainerAtPosition(int containerNo) {
        mPresentContainerList.subList(containerNo, mPresentContainerList.size()).clear();
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
            String dragType = (String) ((Pair) event.getLocalState()).first;

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
                final String dragType = ((String) ((Pair) event.getLocalState()).first);
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
        removeItemList.sort((o1, o2) -> Integer.compare(o2.getContainerNo(), o1.getContainerNo()));
        alertDeleteWarningDialog(view, targetCard, removeItemList, targetContainerPosition);
    }

    public void removeFromCardImageMap(CardDTO[] removeItemArr) {
        if (removeItemArr.length == 0)
            return;

        for (CardDTO testDto : removeItemArr) {
            int testCardNo = testDto.getCardNo();
            if (!cardImageMap.containsKey(testCardNo))
                throw new RuntimeException("#removeFromCardImageMap in loop, but there is no key");

            ObservableBitmap theCardImage = cardImageMap.get(testCardNo);

            if (!cardImageMap.containsValue(theCardImage))
                throw new RuntimeException("#removeFromCardImageMap in loop, but there is no value");

            cardImageMap.remove(theCardImage);
        }
    }

    public synchronized void removeFromAllList(CardDTO[] removeItemArr) {
        Arrays.sort(removeItemArr, (o1, o2) -> Integer.compare(o2.getContainerNo(), o1.getContainerNo()));
        if (removeItemArr.length == 0)
            return;
        for (CardDTO testDto : removeItemArr) {
            final int targetContainerNo = testDto.getContainerNo();
            final int targetRootNo = testDto.getRootNo();
            HashMap<Integer, List<CardDTO>> targetContainerMap = mAllData.get(targetContainerNo);
            if (!targetContainerMap.containsKey(targetRootNo))
                throw new RuntimeException("#removeFromAllList in loop, there is no key");
            List<CardDTO> targetCardList = targetContainerMap.get(targetRootNo);
            if (targetCardList == null)
                throw new RuntimeException("#removeFromAllList in loop, there is no list");
            targetCardList.remove(testDto);
            if (targetCardList.isEmpty()) {
                targetContainerMap.remove(targetRootNo);
            }
            if (targetContainerMap.isEmpty()) {
                mAllData.remove(targetContainerMap);
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
                    removeFromAllList(removeItemArr);
                    removeFromCardImageMap(removeItemArr);
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

    /*
     * Precedent condition : {@param testCardPosition} position item from mPresentData has been removed .
     *
     * */
    public int findNearestItemPosition(int targetContainerPosition, int testCardPosition) {
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
                    removeFromCardImageMap(removeItemArr);
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

    public void findChildrenCards(CardDTO rootCard, List<CardDTO> foundChildrenCollector) {
        final int rootCardNo = rootCard.getCardNo();
        final int testChildContainerNo = rootCard.getContainerNo() + 1;
        final boolean hasNextContainerData = mAllData.size() > testChildContainerNo;
        if (!hasNextContainerData) {
            return;
        }
        List<CardDTO> rootCardChildrenList = mAllData.get(testChildContainerNo).get(rootCardNo);
        if (rootCardChildrenList == null || rootCardChildrenList.isEmpty())
            return;
        foundChildrenCollector.addAll(rootCardChildrenList);
        for (CardDTO childCard : rootCardChildrenList) {
            findChildrenCards(childCard, foundChildrenCollector);
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
            String dragType = (String) ((Pair) event.getLocalState()).first;
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
            dropAndCreateServiceForEmptySpace(containerRecyclerView, rootCardNo, targetContainerPosition);
            return true;
        }
        return false;
    }

    private boolean handleMoveServiceForEmptySpace(TextView targetView, DragEvent event) {
        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) targetView.getParent().getParent();
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (containerLayoutManager == null)
                    return false;
                containerLayoutManager.onDragStart();
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                if (containerLayoutManager == null)
                    return false;
                if (!event.getResult()) {
                    if (!containerLayoutManager.isContainerRollback()) {
                        containerLayoutManager.onDragEnd(
                                createRollbackAction((Pair) ((Pair) event.getLocalState()).second, containerRecyclerView)
                        );
                    }
                }
                return true;
            case DragEvent.ACTION_DROP:
                onMovingCardDroppedInEmptySpace(containerRecyclerView, event);
                return true;
        }
        return false;
    }

    private Runnable createRollbackAction(Pair<CardDTO, Pair<List<CardDTO>, List<CardDTO>>> rollbackData, ContainerRecyclerView containerRecyclerView) {
        return () -> {
            Pair<List<CardDTO>, List<CardDTO>> modifiedData = rollbackData.second;
            CardDTO movingCard = rollbackData.first;
            List<CardDTO> movingCardList = modifiedData.first;
            List<CardDTO> nextCardList = modifiedData.second;
            addToAllData(movingCardList.toArray(new CardDTO[0]));
            final boolean noPresentContainerForMovingCard = mPresentData.size() < movingCard.getContainerNo() + 1;
            addSinglePresentCardDto(movingCard);
            if (noPresentContainerForMovingCard) {
                addContainer(movingCard.getContainerNo(), movingCard.getRootNo());
                ContainerAdapter containerAdapter = (ContainerAdapter) containerRecyclerView.getAdapter();
                if (containerAdapter == null)
                    return;
                containerAdapter.notifyItemInserted(movingCard.getContainerNo());
                if (hasChildInAllData(movingCard)) {
                    throwToMainHandlerWithDelay(() -> {
                        CardRecyclerView targetCardRecyclerView = findCardRecyclerViewFromContainerRecyclerView(containerRecyclerView, movingCard.getContainerNo());
                        if (targetCardRecyclerView != null)
                            presentChildren(targetCardRecyclerView, movingCard.getContainerNo(), movingCard.getSeqNo());
                    }, 260, containerRecyclerView.getContext());
                }
            } else {
                increaseListSeq(nextCardList);
                CardRecyclerView targetCardRecyclerView = findCardRecyclerViewFromContainerRecyclerView(containerRecyclerView, movingCard.getContainerNo());
                if (targetCardRecyclerView == null)
                    return;
                CardAdapter cardAdapter = targetCardRecyclerView.getAdapter();
                if (cardAdapter == null)
                    return;
                cardAdapter.notifyItemInserted(movingCard.getSeqNo());
                targetCardRecyclerView.smoothScrollToPosition(movingCard.getSeqNo());
                presentChildren(targetCardRecyclerView, movingCard.getContainerNo(), movingCard.getSeqNo());
            }
            SingleToastManager.show(SingleToaster.makeTextShort(containerRecyclerView.getContext(), "rollback"));
        };
    }

    public synchronized List<List<Pair<CardDTO, CardState>>> getPresentData() {
        return mPresentData;
    }

    private boolean handleMoveServiceForCardRecyclerView(CardRecyclerView targetView, DragEvent event) {
//        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) targetView.getParent().getParent();
        CardAdapter cardAdapter = targetView.getAdapter();
        if (cardAdapter == null)
            return false;
        int containerPosition = cardAdapter.getPosition();
        if (containerPosition == -1)
            return false;
        final int screenWidth = DisplayUtil.getScreenWidth(targetView.getContext());
        final int MOVE_BOUNDARY_WIDTH = 200;
        CardRecyclerView.ScrollControllableLayoutManager layoutManager = targetView.getLayoutManager();
        if (layoutManager == null)
            return false;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_LOCATION:
                if (layoutManager.isLayoutArrows()) {
                    return false;
                }
                final int cardCount_location = getPresentData().get(containerPosition).size();
                Container container_location = mPresentContainerList.get(containerPosition);
                final int focusedCardPosition_location = container_location.getFocusCardPosition();

                if (event.getX() < MOVE_BOUNDARY_WIDTH && focusedCardPosition_location != 0) {
                    layoutManager.smoothScrollToPosition(focusedCardPosition_location - 1);
                    boolean leftCardArrowNeeded = true;
                    if (focusedCardPosition_location - 1 == 0)
                        leftCardArrowNeeded = false;
                    if (leftCardArrowNeeded)
                        layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_TWO_WAY_ARROW);
                    else
                        layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_RIGHT_ARROW);
                    return true;
                }

                if (event.getX() > screenWidth - MOVE_BOUNDARY_WIDTH && focusedCardPosition_location != cardCount_location - 1) {
                    layoutManager.smoothScrollToPosition(focusedCardPosition_location + 1);
                    boolean rightCardArrowNeeded = true;
                    if (focusedCardPosition_location + 1 == cardCount_location - 1)
                        rightCardArrowNeeded = false;
                    if (rightCardArrowNeeded)
                        layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_TWO_WAY_ARROW);
                    else
                        layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_LEFT_ARROW);
                    return true;
                }
                return true;

            case DragEvent.ACTION_DRAG_STARTED:
                return !(mPresentData.size() < containerPosition + 1);
            case DragEvent.ACTION_DRAG_ENTERED:
                if (containerPosition > mPresentData.size() - 1)
                    return false;
                final int cardCount_entered = getPresentData().get(containerPosition).size();
                Container container_entered = mPresentContainerList.get(containerPosition);
                final int focusedCardPosition_entered = container_entered.getFocusCardPosition();

                boolean leftCardArrowNeeded = false;
                boolean rightCardArrowNeeded = false;
                if (focusedCardPosition_entered != 0) {
                    leftCardArrowNeeded = true;
                }
                if (focusedCardPosition_entered < cardCount_entered - 1) {
                    rightCardArrowNeeded = true;
                }
                if (leftCardArrowNeeded && rightCardArrowNeeded)
                    layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_TWO_WAY_ARROW);
                if (!leftCardArrowNeeded && rightCardArrowNeeded)
                    layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_RIGHT_ARROW);
                if (leftCardArrowNeeded && !rightCardArrowNeeded)
                    layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_LEFT_ARROW);
                if (!leftCardArrowNeeded && !rightCardArrowNeeded)
                    layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_NO_ARROW);
//                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                if (!layoutManager.isMovingDragExited()) {
                    layoutManager.setMovingDragExited(true);
                    layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_NO_ARROW);
                }
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                //post to Handler.
                //cause location event is singled and postDelayed by Handler.
                //cause possibility of already posted set event that horizontal arrow to Visible.
                if (!layoutManager.isMovingDragEnded()) {
                    layoutManager.setMovingDragEnded(true);
                    layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_NO_ARROW);
                }
                return true;
            case DragEvent.ACTION_DROP:
                onMovingCardDroppedInContainer(targetView, event);
                return true;
        }
        return false;
    }


    private boolean hasChildInAllData(CardDTO cardDTO) {
        final int rootCardContainerNo = cardDTO.getContainerNo();
        final int rootCardNo = cardDTO.getCardNo();
        if (!(mAllData.size() > rootCardContainerNo + 1)) {
            return false;
        }
        final boolean hasChildList = mAllData.get(rootCardContainerNo + 1).containsKey(rootCardNo);
        if (hasChildList) {
            List<CardDTO> foundList = mAllData.get(rootCardContainerNo + 1).get(rootCardNo);
            return foundList != null && !foundList.isEmpty();
        }
        return false;
    }

    private void addContainer(int containerPosition, int rootNo) {
        while (mPresentContainerList.size() < containerPosition + 1) {
            mPresentContainerList.add(new Container(rootNo));
        }
    }

    private CardRecyclerView findCardRecyclerViewFromContainerRecyclerView(ContainerRecyclerView containerRecyclerView, int containerPosition) {
        RecyclerView.ViewHolder viewHolder = containerRecyclerView.findViewHolderForAdapterPosition(containerPosition);
        if (viewHolder instanceof CardContainerViewHolder)
            return ((CardContainerViewHolder) viewHolder).getBinding().cardRecyclerview;
        return null;
    }

    // new ArrayList has no exception?
    private void addCardToPresentData(List<CardDTO> cards, int removeBtnVisibility) {
        CardDTO.orderByContainerNoAsc(cards);
        List<Pair<CardDTO, CardState>> pairList = dtoListToPairList(cards, removeBtnVisibility);
        if (pairList.isEmpty())
            return;
        for (Pair<CardDTO, CardState> pair : pairList) {
            final int containerNo = pair.first.getContainerNo();
            final int mPresentDataSize = mPresentData.size();
            if (containerNo + 1 < mPresentDataSize) {
                mPresentData.add(new ArrayList<>());
            }
            mPresentData.get(containerNo).add(pair);
        }
    }

    private boolean handleCreateServiceForContainer(CardRecyclerView targetView, DragEvent event) {
        CardRecyclerView.ScrollControllableLayoutManager layoutManager = targetView.getLayoutManager();
        if (layoutManager == null)
            return false;
        if (event.getAction() == DragEvent.ACTION_DRAG_STARTED)
            return true;
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition == lastVisibleItemPosition) {
            return handleDragEventSingleItemVisibleCase(targetView, firstVisibleItemPosition, event);
        }
        return handleDragEventMultiItemVisibleCase(targetView, firstVisibleItemPosition, lastVisibleItemPosition, event);
    }

    private boolean handleDragEventSingleItemVisibleCase(CardRecyclerView cardRecyclerView, int targetCardPosition, DragEvent event) {
        Logger.message("vm#handleDragEventSingleItemVisibleCase");
        RecyclerView.ViewHolder viewHolder = cardRecyclerView.findViewHolderForAdapterPosition(targetCardPosition);
        if (viewHolder == null)
            return false;
        if (!(viewHolder instanceof ContactCardViewHolder))
            return false;
        ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) viewHolder;
        FrameLayout targetView = cardViewHolder.getBinding().cardContainerFrameLayout;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                slowOut(targetView, false, CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                slowOut(targetView, true, CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DROP:
                slowOut(targetView, true, CARD_LOCATION_LEFT);
                dropAndCreateServiceForContainer(cardRecyclerView, targetView);
                return true;
        }
        return false;
    }

    //Null in lastVisibleViewVH
    private boolean handleDragEventMultiItemVisibleCase(CardRecyclerView rv, int firstVisibleCardPosition, int lastVisibleCardPosition, DragEvent event) {
        Logger.message("vm#handleDragEventMultiItemVisibleCase");
        if (firstVisibleCardPosition == RecyclerView.NO_POSITION)
            return false;
        RecyclerView.ViewHolder firstItemViewHolder = rv.findViewHolderForAdapterPosition(firstVisibleCardPosition);
        RecyclerView.ViewHolder lastItemViewHolder = rv.findViewHolderForAdapterPosition(lastVisibleCardPosition);
        if (firstItemViewHolder == null || lastItemViewHolder == null)
            return false;

        ContactCardViewHolder firstVisibleViewVH = (ContactCardViewHolder) firstItemViewHolder;
        ContactCardViewHolder lastVisibleViewVH = (ContactCardViewHolder) lastItemViewHolder;
        FrameLayout firstVisibleView = firstVisibleViewVH.getBinding().cardContainerFrameLayout;
        FrameLayout lastVisibleView = lastVisibleViewVH.getBinding().cardContainerFrameLayout;
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
        SwitchMaterial removeBtn = findRemoveBtnByContainerRecyclerView(containerRecyclerView);
        int removeBtnVisibility = View.INVISIBLE;
        if (removeBtn.isChecked())
            removeBtnVisibility = View.VISIBLE;
        CardDTO newCardDTO = new CardDTO.Builder().rootNo(rootCardNo).containerNo(targetContainerNo).build();
        mCardRepository.insert(newCardDTO.toEntity()
                , orderDropDataInsertListenerForEmptySpace(containerRecyclerView, targetContainerNo, removeBtnVisibility));
    }

    private SwitchMaterial findRemoveBtnByContainerRecyclerView(RecyclerView containerRecyclerView) {
        return ((ViewGroup) containerRecyclerView.getParent()).findViewById(R.id.main_mode_switch);
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

    //    TODO : loadImage.
    private void initData(int lastContainerNo) {
        Logger.message("vm#setData");
        List<CardDTO> allDTOs = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        List<List<CardDTO>> dataGroupedByContainerNo = groupByContainerNo(allDTOs, lastContainerNo + 1);
        List<HashMap<Integer, List<CardDTO>>> dataGroupedByRootNo = groupByRootNo(dataGroupedByContainerNo);
        orderBySeqNo(dataGroupedByRootNo);
        initContainerList(dataGroupedByRootNo);
        initPresentData(dataGroupedByRootNo);
        initCardImageMap(allDTOs);
        // for Search func
//        mLiveData.postValue(dataGroupedByContainerNo);
        mAllData.clear();
        mAllData.addAll(dataGroupedByRootNo);
    }

    private void initCardImageMap(List<CardDTO> allDtoList){
        for (CardDTO theCardDTO : allDtoList){
            int theCardNo = theCardDTO.getCardNo();
            cardImageMap.put(theCardNo, new ObservableBitmap());
        }
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

    public DropDataInsertListener orderDropDataInsertListenerForEmptySpace(RecyclerView containerRecyclerView, int targetPosition, int removeBtnVisibility) {
        return cardEntity -> {
            mPresentContainerList.add(new Container(cardEntity.getRootNo()));
            mPresentData.add(new ArrayList<>());
            CardDTO newCard = cardEntity.toDTO();
            mPresentData.get(targetPosition).add(Pair.create(newCard, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            final boolean noValueInAllData = mAllData.size() < targetPosition + 1;
            if (noValueInAllData) {
                mAllData.add(new HashMap<>());
            }
            HashMap<Integer, List<CardDTO>> targetMap = mAllData.get(targetPosition);
            if (!targetMap.containsKey(newCard.getRootNo()))
                targetMap.put(newCard.getRootNo(), new ArrayList<>());
            Objects.requireNonNull(targetMap.get(newCard.getRootNo())).add(newCard);
            addCardImageValue(newCard);
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
            CardDTO newCard = foundEntity.toDTO();
            targetItemList.add(newCardSeqNo, Pair.create(foundEntity.toDTO(), new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            final int targetContainerNo = targetDTO.getContainerNo();
            Objects.requireNonNull(mAllData.get(targetContainerNo).get(foundEntity.getRootNo())).add(newCardSeqNo, newCard);
            addCardImageValue(newCard);
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
    public synchronized int getPresentCardCount(int containerPosition) {
        Logger.message("vm#getPresentCardCount");
        if (containerPosition != -1) {
            synchronized (mPresentData) {
                return mPresentData.get(containerPosition).size();
            }
        }
        return 0;
    }

    //sync with allData
    public ObservableBitmap getCardImage(int containerPosition, int cardPosition) {
        int cardNo = getCardDTO(containerPosition, cardPosition).getCardNo();
        return cardImageMap.get(cardNo);
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

//    @BindingAdapter("onTouchListener")
//    public static void setOnTouchListener(View view, View.OnTouchListener listener) {
//        view.setOnTouchListener(listener);
//    }

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

    public void clearPresentContainerList() {
        mPresentContainerList.clear();
    }

    public void clearPresentData() {
        mPresentData.clear();
    }

    public synchronized void presentChildren(RecyclerView cardRecyclerView, int rootContainerPosition, int rootCardPosition) {
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
        if (mPresentContainerList.size() > containerPosition)
            return mPresentContainerList.get(containerPosition);
        return null;
    }

    public List<Pair<CardDTO, CardState>> getTargetPositionPresentData(int containerPos) {
        return mPresentData.get(containerPos);
    }

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