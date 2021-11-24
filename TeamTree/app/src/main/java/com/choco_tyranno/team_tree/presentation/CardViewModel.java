package com.choco_tyranno.team_tree.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableInt;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ActivityMainBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.domain.card_data.CardEntity;
import com.choco_tyranno.team_tree.domain.source.CardRepository;
import com.choco_tyranno.team_tree.presentation.card_rv.CardAdapter;
import com.choco_tyranno.team_tree.presentation.card_rv.CardGestureListener;
import com.choco_tyranno.team_tree.presentation.card_rv.CardRecyclerView;
import com.choco_tyranno.team_tree.presentation.card_rv.CardScrollListener;
import com.choco_tyranno.team_tree.presentation.card_rv.CardState;
import com.choco_tyranno.team_tree.presentation.card_rv.CardTouchListener;
import com.choco_tyranno.team_tree.presentation.card_rv.CardViewShadowProvider;
import com.choco_tyranno.team_tree.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.team_tree.presentation.card_rv.DragMoveDataContainer;
import com.choco_tyranno.team_tree.presentation.card_rv.SpreadingOutDetailOnClickListener;
import com.choco_tyranno.team_tree.presentation.card_rv.ObservableBitmap;
import com.choco_tyranno.team_tree.presentation.card_rv.OnClickListenerForCallBtn;
import com.choco_tyranno.team_tree.presentation.card_rv.OnClickListenerForMessageBtn;
import com.choco_tyranno.team_tree.presentation.card_rv.OnDragListenerForCardRecyclerView;
import com.choco_tyranno.team_tree.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.team_tree.presentation.container_rv.CloneCardShadow;
import com.choco_tyranno.team_tree.presentation.container_rv.Container;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerScrollListener;
import com.choco_tyranno.team_tree.presentation.container_rv.OnDragListenerForBottomArrow;
import com.choco_tyranno.team_tree.presentation.container_rv.OnDragListenerForContainerRecyclerView;
import com.choco_tyranno.team_tree.presentation.container_rv.OnDragListenerForTopArrow;
import com.choco_tyranno.team_tree.presentation.searching_drawer.OnClickListenerForFindingSearchingResultTargetButton;
import com.choco_tyranno.team_tree.presentation.searching_drawer.OnClickListenerForMovingPageBundleBtn;
import com.choco_tyranno.team_tree.presentation.searching_drawer.OnClickListenerForPageBtn;
import com.choco_tyranno.team_tree.presentation.searching_drawer.OnQueryTextListenerForSearchingCard;
import com.choco_tyranno.team_tree.presentation.searching_drawer.SearchingResultAdapter;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class CardViewModel extends AndroidViewModel implements UiThreadAccessible {
    private final CardRepository mCardRepository;
    //    private final MutableLiveData<List<List<CardDto>>> mLiveData;
    private List<HashMap<Integer, List<CardDto>>> mAllData;
    private HashMap<Integer, ObservableBitmap> cardImageMap;
    private List<Container> mPresentContainerList;
    private List<List<Pair<CardDto, CardState>>> mPresentData;

    private MutableLiveData<Boolean> settingsOn;
    private MutableLiveData<Integer> pagerCount;
    private MutableLiveData<Boolean> longPagerOn;
    private List<MutableLiveData<String>> searchResultPagerTextList;

    private List<CardDto> searchingResultCardList;
    private ObservableInt focusPageNo;

    private View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    private View.OnDragListener onDragListenerForCardRecyclerView;
    private View.OnDragListener onDragListenerForContainerRecyclerView;
    private View.OnDragListener onDragListenerForTopArrow;
    private View.OnDragListener onDragListenerForBottomArrow;
    private View.OnDragListener onDragListenerForEmptyCardSpace;
    private View.OnClickListener spreadingOutDetailOnClickListener;
    private View.OnClickListener onClickListenerForCallBtn;
    private View.OnClickListener onClickListenerForMessageBtn;
    private View.OnClickListener onClickListenerForSettingButton;
    private SearchView.OnQueryTextListener onQueryTextListenerForSearchingCard;
    private CardScrollListener.OnFocusChangedListener mOnFocusChangedListener;
    private CardScrollListener.OnScrollStateChangeListener mOnScrollStateChangeListener;
    private ContainerScrollListener mContainerRecyclerViewScrollListener;

    private CardTouchListener cardTouchListener;
    private GestureDetectorCompat cardGestureDetector;
    private CardGestureListener cardGestureListener;
    private SearchingResultAdapter searchingResultAdapter;
    private View.OnClickListener onClickListenerForFindingSearchingResultTargetBtn;
    private View.OnClickListener onClickListenerForPageBtn;
    private View.OnClickListener onClickListenerForMovingPageBundleBtn;

    private final int CARD_LOCATION_LEFT = 0;
    private final int CARD_LOCATION_RIGHT = 1;
    public static final int SEARCHING_RESULT_MAX_COUNT = 5;
    public static final int VISIBLE_PAGE_ITEM_MAX_COUNT = 5;
    private final int LONG_LENGTH_PAGER_NO = 10;
    private final int NO_FOCUS_PAGE = 0;

    private boolean sendingFindCardReq = false;


    public MutableLiveData<Boolean> getLongPagerOn() {
        return longPagerOn;
    }

    public MutableLiveData<Integer> getPagerCount() {
        return pagerCount;
    }

    public void findCurrentOnFocusCardPositions(int containerNo, List<Integer> currentSavedOnFocusCardPositionList) {
        for (int i = 0; i < containerNo; i++) {
            Container container = getContainer(i);
            currentSavedOnFocusCardPositionList.add(container.getFocusCardPosition());
        }
    }

    public void insert(CardDto newCardDto, CardState firstVisibleItemCardState, CardRecyclerView cardRecyclerView) {
        mCardRepository.insert(newCardDto.toEntity()
                , orderDropDataInsertListenerForContainer(firstVisibleItemCardState, mPresentData.get(newCardDto.getContainerNo()), cardRecyclerView)
        );
    }

    public void insertAndUpdates(CardDto newCardDto, CardState firstVisibleItemCardState, CardRecyclerView cardRecyclerView) {
        mCardRepository.insertAndUpdates(
                newCardDto.toEntity()
                , dtoListToEntityList(
                        increaseListCardsSeq(mPresentData.get(newCardDto.getContainerNo()), newCardDto.getSeqNo()))
                , orderDropDataInsertListenerForContainer(firstVisibleItemCardState, mPresentData.get(newCardDto.getContainerNo()), cardRecyclerView)
        );
    }

    public View.OnClickListener getOnClickListenerForSettingButton() {
        return this.onClickListenerForSettingButton;
    }

    public View.OnDragListener getOnDragListenerForContainerRecyclerView() {
        return this.onDragListenerForContainerRecyclerView;
    }

    public View.OnClickListener getOnClickListenerForCallBtn() {
        return this.onClickListenerForCallBtn;
    }

    public View.OnClickListener getOnClickListenerForMessageBtn() {
        return this.onClickListenerForMessageBtn;
    }

    private Pair<Integer, Integer[]> filterUselessScrollUtilData(Integer[] allGoalCardSeqArr) {
        final int presentDataSize = mPresentData.size();
        final int allGoalCardSeqArrLength = allGoalCardSeqArr.length;
        int startFindingContainerPosition = 0;
        for (int i = 0; i < presentDataSize; i++) {
            if (!(i < allGoalCardSeqArrLength))
                break;
            if (getContainer(i).getFocusCardPosition() == allGoalCardSeqArr[i]) {
                allGoalCardSeqArr[i] = null;
                continue;
            }
            startFindingContainerPosition = i;
            break;
        }
        return Pair.create(startFindingContainerPosition, Arrays.stream(allGoalCardSeqArr).filter(Objects::nonNull).toArray(Integer[]::new));
    }

    public Pair<Integer, Integer[]> findScrollUtilDataForFindingOutCard(CardDto cardDto) {
        final int goalContainerSize = cardDto.getContainerNo() + 1;
        final Integer[] goalCardSeqArr = new Integer[goalContainerSize];
        goalCardSeqArr[cardDto.getContainerNo()] = cardDto.getSeqNo();
        int testRootNo = cardDto.getRootNo();
        int testContainerNo = cardDto.getContainerNo() - 1;

        for (int i = testContainerNo; i > -1; i--) {
            HashMap<Integer, List<CardDto>> theContainerDataMap = mAllData.get(i);
            for (int key : theContainerDataMap.keySet()) {
                boolean targetFound = false;
                List<CardDto> testCardDtoList = theContainerDataMap.get(key);
                if (testCardDtoList == null)
                    throw new RuntimeException("vm#findScrollUtilDataForFindingOutCard - testCardDtoList==null");
                for (CardDto testCard : testCardDtoList) {
                    if (testCard.getCardNo() == testRootNo) {
                        goalCardSeqArr[i] = testCard.getSeqNo();
                        testRootNo = testCard.getRootNo();
                        targetFound = true;
                        break;
                    }
                }
                if (targetFound)
                    break;
            }
        }
        return filterUselessScrollUtilData(goalCardSeqArr);
    }


    /*
     *
     *
     * 1. findCardContainers => containerPosition, cardPosition
     * 2. find start container to be changed FocusCardNo. => make Runnable with delayed.
     * 3. change each focusSeqNo
     * 4.run scrollToPosition actions.
     * -container scroll / card scroll.
     * 5. At last find card scroll with Toast "카드를 찾았습니다.".
     *
     * */
    public void requestFindingOutCard(CardDto card) {
        final int targetContainerNo = card.getContainerNo();
        final int targetCardSeqNo = card.getSeqNo();

    }

    public boolean isSendingFindCardReq() {
        return sendingFindCardReq;
    }

    public void setSendingFlag(boolean flag) {
        sendingFindCardReq = flag;
    }

    private int countAllPage() {
        final int allItemCount = searchingResultCardList.size();
        if (allItemCount == 0)
            return 0;
        int allPageCount = Math.floorDiv(allItemCount, SEARCHING_RESULT_MAX_COUNT);
        boolean hasRemainder = Math.floorMod(allItemCount, SEARCHING_RESULT_MAX_COUNT) > 0;
        if (hasRemainder)
            allPageCount += 1;
        return allPageCount;
    }

    public int countBaseMaxPageBundle() {
        final int focusPageNo = getFocusPageNo();
        int baseMaxPageBundleCount = Math.floorDiv(focusPageNo, VISIBLE_PAGE_ITEM_MAX_COUNT);
        final boolean noRemainderPage = Math.floorMod(focusPageNo, VISIBLE_PAGE_ITEM_MAX_COUNT) == 0;
        if (noRemainderPage) {
            baseMaxPageBundleCount--;
        }
        return baseMaxPageBundleCount;
    }

    public boolean hasPrevPageBundle() {
        return getPageBundleNoByPageNo(getFocusPageNo()) > 1;
//        final int baseMaxPageBundleCount = countBaseMaxPageBundle();
//        return baseMaxPageBundleCount > 0;
    }

    public boolean hasNextPageBundle() {
        final int allPageLastNo = countAllPage();
        final int currentFocusPageNo = getFocusPageNo();
        final int maxPageBundleNo = getPageBundleNoByPageNo(allPageLastNo);
        final int currentPageBundleNo = getPageBundleNoByPageNo(currentFocusPageNo);
        return currentPageBundleNo < maxPageBundleNo;
    }

    private int getPageBundleNoByPageNo(int pageNo) {
        int pageBundleNo = 0;
        final int quotient = Math.floorDiv(pageNo, VISIBLE_PAGE_ITEM_MAX_COUNT);
        final boolean hasRemainder = Math.floorMod(pageNo, VISIBLE_PAGE_ITEM_MAX_COUNT) != 0;
        pageBundleNo = quotient;
        if (hasRemainder)
            pageBundleNo++;
        return pageBundleNo;
    }


    public View.OnClickListener getOnClickListenerForMovingPageBundleBtn() {
        return this.onClickListenerForMovingPageBundleBtn;
    }

    public View.OnClickListener getOnClickListenerForPageBtn() {
        return this.onClickListenerForPageBtn;
    }

    public void setFocusPageNo(int pageNo) {
        focusPageNo.set(pageNo);
    }

    public int getFocusPageNo() {
        return focusPageNo.get();
    }

    public int getCurrentPageCount() {
        final int allItemCount = searchingResultCardList.size();
        if (allItemCount == 0)
            return 0;
        final int maxItemPageCount = Math.floorDiv(allItemCount, SEARCHING_RESULT_MAX_COUNT);
        int allPageCount = 0;
        allPageCount += maxItemPageCount;
        boolean hasRemainder = Math.floorMod(allItemCount, SEARCHING_RESULT_MAX_COUNT) > 0;
        if (hasRemainder)
            allPageCount++;
        final int focusPageNo = getFocusPageNo();
        final int maxItemPageBundleCount = Math.floorDiv(focusPageNo, VISIBLE_PAGE_ITEM_MAX_COUNT);
        int baseMaxItemPageBundleCount = 0;
        baseMaxItemPageBundleCount += maxItemPageBundleCount;
        final boolean noRemainderPage = Math.floorMod(focusPageNo, VISIBLE_PAGE_ITEM_MAX_COUNT) == 0;
        if (noRemainderPage) {
            baseMaxItemPageBundleCount--;
        }
        int basePageCount = baseMaxItemPageBundleCount * VISIBLE_PAGE_ITEM_MAX_COUNT;
        final int nextPagesCount = allPageCount - basePageCount;
        return Math.min(nextPagesCount, VISIBLE_PAGE_ITEM_MAX_COUNT);
    }

    /*
     * item with start 1.
     * no item : 0.
     * */
    public void resetFocusPageNo() {
        focusPageNo.set(NO_FOCUS_PAGE);
        if (searchingResultCardList.size() > 0)
            focusPageNo.set(1);
    }

    public CardDto getSearchingResultCard(int pageNo, int itemPosition) {
        if (pageNo < 1)
            throw new RuntimeException(" (bind) => vm#getSearchingResultCard - pageNo < 1");
        final int allItemCount = searchingResultCardList.size();
        final int baseItemCount = (pageNo - 1) * SEARCHING_RESULT_MAX_COUNT;
        final int nextItemCount = allItemCount - baseItemCount;
        final int itemCount = Math.min(nextItemCount, SEARCHING_RESULT_MAX_COUNT);
        List<CardDto> subList = searchingResultCardList.subList(baseItemCount, baseItemCount + itemCount);
        if (!(subList.size() > itemPosition)) {
            return null;
        }
        return subList.get(itemPosition);
    }

    public void searchCards(String queryText) {
        searchingResultCardList.clear();
        if (TextUtils.equals(queryText, ""))
            return;
        List<CardDto> allCards = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        String precessedText = queryText.toLowerCase(Locale.getDefault());
        for (CardDto testCard : allCards) {
            boolean containText = false;
            if (testCard.getTitle().toLowerCase(Locale.getDefault()).contains(precessedText)) {
                containText = true;
            }
            if (testCard.getContactNumber().contains(precessedText)) {
                containText = true;
            }
            if (containText)
                searchingResultCardList.add(testCard);
        }
        searchingResultCardList.sort((o1, o2) -> Integer.compare(o1.getContainerNo(), o2.getContainerNo()));
    }

    public View.OnClickListener getOnClickListenerForFindingSearchingResultTargetBtn() {
        return onClickListenerForFindingSearchingResultTargetBtn;
    }

    public int getSearchingResultItemCount() {
        final int allItemCount = searchingResultCardList.size();
        if (allItemCount == 0)
            return 0;
        final int baseItemCount = (focusPageNo.get() - 1) * SEARCHING_RESULT_MAX_COUNT;
        final int nextItemCount = allItemCount - baseItemCount;
        return Math.min(nextItemCount, SEARCHING_RESULT_MAX_COUNT);
    }

    public SearchingResultAdapter getSearchingResultRecyclerViewAdapter() {
        return searchingResultAdapter;
    }

    @BindingAdapter("recyclerViewAdapter")
    public static void setRecyclerViewAdapter(RecyclerView view, RecyclerView.Adapter adapter) {
        view.setAdapter(adapter);
    }

    public void setPictureCardImage(Bitmap resource, int cardNo) {
        ObservableBitmap theImageHolder = cardImageMap.get(cardNo);
        if (theImageHolder != null)
            theImageHolder.setCardThumbnail(resource);
    }

    public CardDto[] getPictureCardArr() {
        return mCardRepository.getData().stream().map(CardEntity::toDTO).filter(cardDto -> !TextUtils.equals(cardDto.getImagePath(), "")).toArray(CardDto[]::new);
    }

    public boolean applyCardFromDetailActivity(CardDto updatedCardDto) {
        boolean result = false;
        CardDto mainCardDto = getCardDto(updatedCardDto.getContainerNo(), updatedCardDto.getSeqNo());
        if (!TextUtils.equals(mainCardDto.getTitle(), updatedCardDto.getTitle()))
            mainCardDto.setTitle(updatedCardDto.getTitle());
        if (!TextUtils.equals(mainCardDto.getSubtitle(), updatedCardDto.getSubtitle()))
            mainCardDto.setSubtitle(updatedCardDto.getSubtitle());
        if (!TextUtils.equals(mainCardDto.getContactNumber(), updatedCardDto.getContactNumber()))
            mainCardDto.setContactNumber(updatedCardDto.getContactNumber());
        if (!TextUtils.equals(mainCardDto.getFreeNote(), updatedCardDto.getFreeNote()))
            mainCardDto.setFreeNote(updatedCardDto.getFreeNote());
        if (!TextUtils.equals(mainCardDto.getImagePath(), updatedCardDto.getImagePath())) {
            mainCardDto.setImagePath(updatedCardDto.getImagePath());
            result = true;
        }
        return result;
    }

    public void addCardImageValue(CardDto cardDto) {
        cardImageMap.put(cardDto.getCardNo(), new ObservableBitmap());
    }

    public boolean hasDefaultCardImage() {
        return cardImageMap.containsKey(CardDto.NO_ROOT_CARD);
    }

    public Bitmap getDefaultCardThumbnail() {

        return Objects.requireNonNull(cardImageMap.get(CardDto.NO_ROOT_CARD)).getThumbnail();
    }

    @BindingAdapter(value = {"cardThumbnail", "defaultCardThumbnail"})
    public static void loadCardThumbnail(ImageView view, Bitmap cardThumbnail, Bitmap defaultCardThumbnail) {
        if (cardThumbnail != null)
            view.setImageBitmap(cardThumbnail);
        else
            view.setImageBitmap(defaultCardThumbnail);
    }

    public void setDefaultCardImage(Bitmap defaultCardImage) {
        cardImageMap.put(CardDto.NO_ROOT_CARD, new ObservableBitmap(defaultCardImage));
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

    public View.OnTouchListener getOnTouchListenerForCard() {
        return cardTouchListener;
    }

    @BindingAdapter("onCardTouchListener")
    public static void setOnCardTouchListener(View view, View.OnTouchListener touchListener) {
        view.setOnTouchListener(touchListener);
    }

    @BindingAdapter("onClickListener")
    public static void setOnClickListener(View view, View.OnClickListener clickListener) {
        view.setOnClickListener(clickListener);
    }

    public View.OnClickListener getSpreadingOutDetailOnClickListener() {
        return spreadingOutDetailOnClickListener;
    }

    /*
     * DO NEXT : flip card.
     *
     * */

    /* Default constructor*/

    public CardViewModel(Application application) {
        super(application);
        Logger.message("VM#constructor");
        this.mCardRepository = new CardRepository(application);
//        this.mLiveData = new MutableLiveData<>();
        this.mAllData = new ArrayList<>();
        this.mPresentData = new ArrayList<>();
        this.mPresentContainerList = new ArrayList<>();
        this.cardImageMap = new HashMap<>();
//        this.searchingQueryText = new ObservableString();
        this.searchingResultCardList = new ArrayList<>();
        this.searchingResultAdapter = new SearchingResultAdapter(this);
        focusPageNo = new ObservableInt(NO_FOCUS_PAGE);
        this.settingsOn = new MutableLiveData<>(false);
        this.longPagerOn = new MutableLiveData<>(false);
        this.pagerCount = new MutableLiveData<>(0);
        this.searchResultPagerTextList = new ArrayList<>();
        for (int i = 0; i < VISIBLE_PAGE_ITEM_MAX_COUNT; i++) {
            searchResultPagerTextList.add(new MutableLiveData<>(""));
        }
        initListeners();
    }

    public MutableLiveData<String> getSearchResultPager1Text() {
        return searchResultPagerTextList.get(0);
    }

    public MutableLiveData<String> getSearchResultPager2Text() {
        return searchResultPagerTextList.get(1);
    }

    public MutableLiveData<String> getSearchResultPager3Text() {
        return searchResultPagerTextList.get(2);
    }

    public MutableLiveData<String> getSearchResultPager4Text() {
        return searchResultPagerTextList.get(3);
    }

    public MutableLiveData<String> getSearchResultPager5Text() {
        return searchResultPagerTextList.get(4);
    }

    // ***** Start Listener
    private void initListeners() {
        initCreateCardUtilFabOnLongClickListener();
        initCardRecyclerViewDragListener();
        initEmptyCardSpaceDragListener();
        initOnFocusChangedListener();
        initOnScrollStateChangeListener();
        initScrollListenerForContainerRecyclerView();
        initCardTouchListener();
        initSpreadingOutDetailOnClickListener();
        initOnQueryTextListenerForSearchingCard();
        initOnClickListenerForFindingSearchingResultTargetBtn();
        initOnClickListenerForPageBtn();
        initOnClickListenerForMovingPageBundleBtn();
        initOnClickListenerForCallBtn();
        initOnClickListenerForMessageBtn();
        initOnDragListenerForContainerRecyclerView();
        initOnDragListenerForTopArrow();
        initOnDragListenerForBottomArrow();
        initOnClickListenerForSettingButton();
    }

    private void initOnClickListenerForSettingButton() {
        this.onClickListenerForSettingButton = view -> {
            ActivityMainBinding binding = ((MainCardActivity) view.getContext()).getMainBinding();
            DrawerLayout mainDL = binding.drawerLayoutMainSearchDrawer;
            mainDL.closeDrawer(GravityCompat.END);
            toggleSettingsOn();

            ActionBar appBar = ((MainCardActivity) view.getContext()).getSupportActionBar();
            Objects.requireNonNull(appBar).setTitle("설정");
            appBar.setDisplayHomeAsUpEnabled(true);
            appBar.show();
        };
    }

    private void initScrollListenerForContainerRecyclerView() {
        this.mContainerRecyclerViewScrollListener = new ContainerScrollListener();
    }

    private void initOnDragListenerForBottomArrow() {
        this.onDragListenerForBottomArrow = new OnDragListenerForBottomArrow();
    }

    private void initOnDragListenerForTopArrow() {
        this.onDragListenerForTopArrow = new OnDragListenerForTopArrow();
    }

    private void initOnDragListenerForContainerRecyclerView() {
        this.onDragListenerForContainerRecyclerView = new OnDragListenerForContainerRecyclerView();
    }

    private void initOnClickListenerForMessageBtn() {
        this.onClickListenerForMessageBtn = new OnClickListenerForMessageBtn();
    }

    private void initOnClickListenerForCallBtn() {
        this.onClickListenerForCallBtn = new OnClickListenerForCallBtn();
    }

    private void initOnClickListenerForMovingPageBundleBtn() {
        this.onClickListenerForMovingPageBundleBtn = new OnClickListenerForMovingPageBundleBtn();
    }

    private void initOnClickListenerForPageBtn() {
        this.onClickListenerForPageBtn = new OnClickListenerForPageBtn();
    }

    private void initOnClickListenerForFindingSearchingResultTargetBtn() {
        this.onClickListenerForFindingSearchingResultTargetBtn = new OnClickListenerForFindingSearchingResultTargetButton();
    }

    private void initOnQueryTextListenerForSearchingCard() {
        this.onQueryTextListenerForSearchingCard = new OnQueryTextListenerForSearchingCard(this);
    }

    private void initSpreadingOutDetailOnClickListener() {
        spreadingOutDetailOnClickListener = new SpreadingOutDetailOnClickListener();
    }

    private void initCardTouchListener() {
        cardTouchListener = new CardTouchListener();
    }

    private void initCreateCardUtilFabOnLongClickListener() {
        this.onLongListenerForCreateCardUtilFab = (view) -> view.startDragAndDrop(
                ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), null))
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
        ItemCardframeBinding cardFrameBinding = ((ContactCardViewHolder) cardRecyclerView.getChildViewHolder(prevSeqCardView)).getBinding();
        CardDto prevSeqCardDto = cardFrameBinding.getCard();
        CardState prevCardState = cardFrameBinding.getCardState();
        int prevCardSeqNo = prevSeqCardDto.getSeqNo();
        int rootNo = prevSeqCardDto.getRootNo();
        int containerNo = prevSeqCardDto.getContainerNo();
        List<Pair<CardDto, CardState>> targetContainerCardList = mPresentData.get(containerNo);
        CardDto newCardDTO = new CardDto.Builder().seqNo(prevCardSeqNo + 1).rootNo(rootNo).containerNo(containerNo).build();
        if (targetContainerCardList.size() > prevCardSeqNo + 1) {
            mCardRepository.insertAndUpdates(
                    newCardDTO.toEntity()
                    , dtoListToEntityList(
                            increaseListCardsSeq(targetContainerCardList, prevCardSeqNo + 1))
                    , orderDropDataInsertListenerForContainer(prevCardState, targetContainerCardList, cardRecyclerView)
            );
        } else {
            mCardRepository.insert(newCardDTO.toEntity()
                    , orderDropDataInsertListenerForContainer(prevCardState, targetContainerCardList, cardRecyclerView)
            );
        }
    }

    public void increaseListSeq(List<CardDto> list) {
        if (list == null || list.isEmpty())
            return;
        for (CardDto cardDto : list) {
            cardDto.setSeqNo(cardDto.getSeqNo() + 1);
        }
    }

    public synchronized void reduceListSeq(List<CardDto> list) {
        if (list == null || list.isEmpty())
            return;
        for (CardDto cardDTO : list) {
            cardDTO.setSeqNo(cardDTO.getSeqNo() - 1);
        }
    }

    private void onMovingCardDroppedInEmptySpace(ContainerRecyclerView containerRecyclerView, DragEvent event) {
        DragMoveDataContainer dragMoveDataContainer = (DragMoveDataContainer) event.getLocalState();
        CardDto movingRootCard = dragMoveDataContainer.getRootCard();
        List<CardDto> movingCards = dragMoveDataContainer.getMovingCardList();
        List<CardDto> nextCards = dragMoveDataContainer.getPastLocationNextCardList();
        if (containerRecyclerView == null)
            return;
        LinearLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        if (containerLayoutManager == null)
            return;
        final int containerRecyclerViewItemCount = containerLayoutManager.getItemCount();
        final int targetContainerPosition = containerRecyclerViewItemCount - 1;
        Runnable uiUpdateAction = () -> {
            addToAllData(movingCards.toArray(new CardDto[0]));
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

        List<CardDto> dataToUpdate = new ArrayList<>();
        dataToUpdate.addAll(movingCards);
        dataToUpdate.addAll(nextCards);

        movingRootCard.setSeqNo(Container.DEFAULT_CARD_POSITION);
        final int adjContainerAmount = targetContainerPosition - movingRootCard.getContainerNo();

        for (CardDto movingCard : movingCards) {
            movingCard.setContainerNo(movingCard.getContainerNo() + adjContainerAmount);
        }

        if (targetContainerPosition == 0) {
            movingRootCard.setRootNo(CardDto.NO_ROOT_CARD);
            mCardRepository.update(dtoListToEntityList(dataToUpdate), uiUpdateAction);
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
                }
            };
            throwToMainHandlerWithDelay(findAboveContainerAndSetRootNoAction, 250, containerRecyclerView.getContext());
        }
    }

    private void onMovingCardDroppedInContainer(@NonNull CardRecyclerView targetContainerCardRecyclerView, DragEvent event) {
        DragMoveDataContainer dragMoveDataContainer = (DragMoveDataContainer) event.getLocalState();
        CardDto movingRootCard = dragMoveDataContainer.getRootCard();
        List<CardDto> movingCards = dragMoveDataContainer.getMovingCardList();
        List<CardDto> pastNextCards = dragMoveDataContainer.getPastLocationNextCardList();
        CardAdapter targetContainerCardAdapter = targetContainerCardRecyclerView.getAdapter();
        if (targetContainerCardAdapter == null)
            return;
        final int targetContainerPosition = targetContainerCardAdapter.getContainerPosition();
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
        List<CardDto> targetContainerNextCards = new ArrayList<>();
        findNextCards(targetContainerPosition, targetContainerFocusCardPosition - 1, targetContainerNextCards);
        increaseListSeq(targetContainerNextCards);
        int targetContainerRootNo = targetContainer.getRootNo();
        if (targetContainerRootNo == Container.NO_ROOT_NO)
            throw new RuntimeException("[now work]targetContainerRootNo == Container.NO_ROOT_NO / #onMovingCardDroppedInContainer /");
        movingRootCard.setRootNo(targetContainer.getRootNo());
        movingRootCard.setSeqNo(targetContainerFocusCardPosition);
        final int adjustContainerNoCount = targetContainerPosition - movingRootCard.getContainerNo();
        adjustListContainerNo(movingCards, adjustContainerNoCount);
        //fin

        //updateData
        List<CardDto> dataToUpdate = new ArrayList<>();
        dataToUpdate.addAll(movingCards);
        dataToUpdate.addAll(pastNextCards);
        dataToUpdate.addAll(targetContainerNextCards);
        //movingCards, nextCards, targetContainerNextCards.
        //->
        Runnable uiUpdateAction = () -> {
            //add movingCards into AllData
            addToAllData(movingCards.toArray(new CardDto[0]));

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

    public void adjustListContainerNo(List<CardDto> cards, int adjustCount) {
        if (cards == null)
            return;
        for (CardDto card : cards) {
            card.setContainerNo(card.getContainerNo() + adjustCount);
        }
    }

    public void addToAllData(CardDto[] cardArr) {
        if (cardArr == null || cardArr.length == 0)
            return;
        Arrays.sort(cardArr, (o1, o2) -> Integer.compare(o1.getContainerNo(), o2.getContainerNo()));
        final int lastContainerPosition = cardArr[cardArr.length - 1].getContainerNo();
        if (mAllData.size() < lastContainerPosition + 1) {
            while (mAllData.size() < lastContainerPosition + 1) {
                mAllData.add(new HashMap<>());
            }
        }
        for (CardDto cardDTO : cardArr) {
            final int targetContainerPosition = cardDTO.getContainerNo();
            final int targetRootNo = cardDTO.getRootNo();
            final HashMap<Integer, List<CardDto>> targetMap = mAllData.get(targetContainerPosition);

            if (!targetMap.containsKey(targetRootNo)) {
                targetMap.put(targetRootNo, new ArrayList<>());
            }
            List<CardDto> targetList = targetMap.get(targetRootNo);
            Objects.requireNonNull(targetList).add(cardDTO);
            Collections.sort(targetList);
        }
    }

    /*
     * @param flagCardPosition is exclude position.
     * */
    public void findNextCards(int containerPosition, int flagCardPosition, List<CardDto> foundItemCollector) {
        if (mPresentData.size() < containerPosition + 1)
            return;
        List<Pair<CardDto, CardState>> targetContainerCards = mPresentData.get(containerPosition);
        if (targetContainerCards.isEmpty())
            return;
        if (targetContainerCards.size() > flagCardPosition + 1) {
            List<Pair<CardDto, CardState>> filteredSubList = targetContainerCards.subList(flagCardPosition + 1, targetContainerCards.size());
            foundItemCollector.addAll(pairListToCardDtoList(filteredSubList));
        }
    }

    /*
     * return :  Has item after item removed from mPresentData.
     * */
    public synchronized boolean removeSinglePresentCardDto(CardDto cardDTO) {
        final int containerNo = cardDTO.getContainerNo();
        final int seqNo = cardDTO.getSeqNo();
        mPresentData.get(containerNo).remove(seqNo);
        return !mPresentData.get(containerNo).isEmpty();
    }

    public void addSinglePresentCardDto(CardDto cardDto) {
        final int containerNo = cardDto.getContainerNo();
        final int seqNo = cardDto.getSeqNo();
        if (mPresentData.size() < containerNo + 1) {
            mPresentData.add(new ArrayList<>());
        }
        mPresentData.get(containerNo).add(seqNo, Pair.create(cardDto, new CardState()));
    }

    public synchronized void clearContainerPositionPresentData(int startContainerPosition) {
        mPresentData.subList(startContainerPosition, mPresentData.size()).clear();
    }

    private List<CardDto> pairListToCardDtoList(List<Pair<CardDto, CardState>> pairList) {
        return pairList.stream().map(pair -> pair.first).collect(Collectors.toList());
    }


    public synchronized void clearContainerAtPosition(int containerNo) {
        mPresentContainerList.subList(containerNo, mPresentContainerList.size()).clear();
    }

    private void initCardRecyclerViewDragListener() {
        onDragListenerForCardRecyclerView = new OnDragListenerForCardRecyclerView();
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
                CardRecyclerView cardRecyclerView = (CardRecyclerView) view;
                ContainerRecyclerView containerRecyclerView = cardRecyclerView.getContainerRecyclerView();
                if (containerRecyclerView.isOnDragMove()) {
                    cardRecyclerView.postChangingFocusAction(() -> {
                        mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                        presentChildren(view, containerPosition, cardPosition);
                    });
                    return;
                }
                mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                presentChildren(view, containerPosition, cardPosition);
            }

            @Override
            public void onPreviousFocused(RecyclerView view, int containerPosition, int cardPosition) {
                CardRecyclerView cardRecyclerView = (CardRecyclerView) view;
                ContainerRecyclerView containerRecyclerView = cardRecyclerView.getContainerRecyclerView();
                if (containerRecyclerView.isOnDragMove()) {
                    cardRecyclerView.postChangingFocusAction(() -> {
                        mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                        presentChildren(view, containerPosition, cardPosition);
                    });
                    return;
                }
                mPresentContainerList.get(containerPosition).setFocusCardPosition(cardPosition);
                presentChildren(view, containerPosition, cardPosition);
            }
        };
    }

    public void toggleSettingsOn() {
        Optional.of(settingsOn).ifPresent(settingsOn -> Optional.ofNullable(settingsOn.getValue()).ifPresent(value -> settingsOn.setValue(!value)));
    }

    public MutableLiveData<Boolean> isSettingsOn() {
        return this.settingsOn;
    }

    public CardScrollListener.OnScrollStateChangeListener getOnScrollStateChangeListener() {
        return mOnScrollStateChangeListener;
    }


    /* remove card */

    public void onRemoveBtnClicked(View view, CardDto targetCard) {
        int targetContainerPosition = findContainerPositionByRemoveBtn(view);
        List<CardDto> removeItemList = new ArrayList<>();
        findChildrenCards(targetCard, removeItemList);
        removeItemList.add(targetCard);
        removeItemList.sort((o1, o2) -> Integer.compare(o2.getContainerNo(), o1.getContainerNo()));
        alertDeleteWarningDialog(view, targetCard, removeItemList, targetContainerPosition);
    }

    public void removeFromCardImageMap(CardDto[] removeItemArr) {
        if (removeItemArr.length == 0)
            return;

        for (CardDto testDto : removeItemArr) {
            int testCardNo = testDto.getCardNo();
            cardImageMap.remove(testCardNo);
        }
    }

    public synchronized void removeFromAllList(CardDto[] removeItemArr) {
        Arrays.sort(removeItemArr, (o1, o2) -> Integer.compare(o2.getContainerNo(), o1.getContainerNo()));
        if (removeItemArr.length == 0)
            return;
        for (CardDto testDto : removeItemArr) {
            final int targetContainerNo = testDto.getContainerNo();
            final int targetRootNo = testDto.getRootNo();
            HashMap<Integer, List<CardDto>> targetContainerMap = mAllData.get(targetContainerNo);
            if (!targetContainerMap.containsKey(targetRootNo))
                throw new RuntimeException("#removeFromAllList in loop, there is no key");
            List<CardDto> targetCardList = targetContainerMap.get(targetRootNo);
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


    private void handleRemoveOneLeftTargetCard(View view, CardDto cardDto, List<CardDto> removeItemList, int targetContainerPosition) {
        CardDto[] removeItemArr = removeItemList.toArray(new CardDto[0]);
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
    private void handleRemoveTargetCardInCrowds(View view, CardDto cardDto, List<CardDto> removeItemList, List<CardDto> updateItemList, int targetContainerPosition) {
        CardDto[] removeItemArr = removeItemList.toArray(new CardDto[0]);
        Logger.message("before work : updateItemList : " + updateItemList.size());
        Logger.message("before work : removeItemList : " + removeItemList.size());
        for (CardDto updateCard : updateItemList) {
            updateCard.setSeqNo(updateCard.getSeqNo() - 1);
        }
        boolean focusedTarget = isFocusedItem(targetContainerPosition, cardDto.getSeqNo());
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
                    mPresentData.get(targetContainerPosition).remove(cardDto.getSeqNo());
                    if (focusedTarget) {
                        runOnUiThread(() -> {
                            RecyclerView targetCardRecyclerView = getCardRecyclerViewFromRemoveButton(view);
                            CardAdapter cardAdapter = (CardAdapter) targetCardRecyclerView.getAdapter();
                            if (cardAdapter == null)
                                return;
                            cardAdapter.notifyItemRemoved(cardDto.getSeqNo());
                            int newFocusPosition = findNearestItemPosition(targetContainerPosition, cardDto.getSeqNo());
                            mPresentContainerList.get(targetContainerPosition).setFocusCardPosition(newFocusPosition);
                            presentChildren(targetCardRecyclerView, targetContainerPosition, newFocusPosition);
                            SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "요청한 카드가 삭제되었습니다."));
                        }, view.getContext());
                    } else {
                        runOnUiThread(() -> {
                            CardAdapter cardAdapter = (CardAdapter) getCardRecyclerViewFromRemoveButton(view).getAdapter();
                            if (cardAdapter == null)
                                return;
                            cardAdapter.notifyItemRemoved(cardDto.getSeqNo());
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
        ConstraintLayout cardFrame = (ConstraintLayout) view.getParent().getParent();
        RecyclerView cardRecyclerView = (RecyclerView) cardFrame.getParent();
        ConstraintLayout containerFrame = (ConstraintLayout) cardRecyclerView.getParent();
        RecyclerView containerRecyclerView = (RecyclerView) containerFrame.getParent();
        return containerRecyclerView.getChildAdapterPosition(containerFrame);
    }

    public void findChildrenCards(CardDto rootCard, List<CardDto> foundChildrenCollector) {
        final int rootCardNo = rootCard.getCardNo();
        final int testChildContainerNo = rootCard.getContainerNo() + 1;
        final boolean hasNextContainerData = mAllData.size() > testChildContainerNo;
        if (!hasNextContainerData) {
            return;
        }
        List<CardDto> rootCardChildrenList = mAllData.get(testChildContainerNo).get(rootCardNo);
        if (rootCardChildrenList == null || rootCardChildrenList.isEmpty())
            return;
        foundChildrenCollector.addAll(rootCardChildrenList);
        for (CardDto childCard : rootCardChildrenList) {
            findChildrenCards(childCard, foundChildrenCollector);
        }
    }

    private void alertDeleteWarningDialog(View view, CardDto targetCardDto, List<CardDto> removeItemList, int targetContainerPosition) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext());
        String targetTitle = targetCardDto.getTitle();
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
                        handleRemoveOneLeftTargetCard(view, targetCardDto, removeItemList, targetContainerPosition);
                    else {
                        findUpdateItems(targetContainerPosition, targetCardDto.getSeqNo());
                        handleRemoveTargetCardInCrowds(view, targetCardDto
                                , removeItemList
                                , findUpdateItems(targetContainerPosition, targetCardDto.getSeqNo())
                                , targetContainerPosition);
                    }
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    SingleToastManager.show(SingleToaster.makeTextShort(view.getContext(), "요청이 취소됐습니다."));
                    dialog.cancel();
                });
        AlertDialog alertDialog = alertBuilder.create();
        runOnUiThread(() -> {
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(view.getResources().getColor(R.color.defaultTextColor, view.getContext().getTheme()));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(view.getResources().getColor(R.color.defaultTextColor, view.getContext().getTheme()));
        }, view.getContext());
    }

    private List<CardDto> findUpdateItems(int targetContainerPosition, int targetCardPosition) {
        List<CardDto> foundItems = new ArrayList<>();
        List<Pair<CardDto, CardState>> updateList =
                mPresentData.get(targetContainerPosition).subList(targetCardPosition + 1, mPresentData.get(targetContainerPosition).size());
        for (Pair<CardDto, CardState> pair : updateList) {
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

        for (List<Pair<CardDto, CardState>> containerItems : mPresentData) {
            for (Pair<CardDto, CardState> item : containerItems) {
                item.second.setRemoveBtnVisibility(newVisibility);
            }
        }
    }

    /* Drag and drop for add new card*/

    private static final int CARD_RECYCLERVIEW = 1;
    private static final int EMPTY_CARD_SPACE = 2;

    public void initEmptyCardSpaceDragListener() {
        onDragListenerForEmptyCardSpace = (view, event) -> {
            if (view.getId() != R.id.view_emptyContainer_cardSpace)
                return false;

            String dragType = "";
            if (event.getLocalState() instanceof Pair)
                dragType = (String) ((Pair) event.getLocalState()).first;
            if (event.getLocalState() instanceof DragMoveDataContainer)
                dragType = ((DragMoveDataContainer) event.getLocalState()).getDragType();
            if (TextUtils.equals(dragType, "CREATE")) {
                return handleCreateServiceForEmptySpace(view, event);
            }
            if (TextUtils.equals(dragType, DragMoveDataContainer.DRAG_TYPE)) {
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
            Animation shakeAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.card_shaking);
            view.startAnimation(shakeAnimation);
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
            int rootCardNo = CardDto.NO_ROOT_CARD;
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
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DROP:
                onMovingCardDroppedInEmptySpace(containerRecyclerView, event);
                return true;
        }
        return false;
    }

    public Runnable createRollbackAction(DragMoveDataContainer dragMoveDataContainer, ContainerRecyclerView containerRecyclerView) {
        return () -> {
            CardDto rootCard = dragMoveDataContainer.getRootCard();
            List<CardDto> movingCardList = dragMoveDataContainer.getMovingCardList();
            List<CardDto> nextCardList = dragMoveDataContainer.getPastLocationNextCardList();
            List<Integer> pastOnFocusPositionList = dragMoveDataContainer.getPastOnFocusPositionList();
            final int rootCardContainerPosition = rootCard.getContainerNo();
            ContainerRecyclerView.ItemScrollingControlLayoutManager containerRecyclerViewLayoutManager = containerRecyclerView.getLayoutManager();
            if (containerRecyclerViewLayoutManager == null)
                return;
            Runnable afterAboveContainersRollbackAction = () -> {
                addToAllData(movingCardList.toArray(new CardDto[0]));
                final boolean noPresentContainerForMovingCard = mPresentData.size() < rootCard.getContainerNo() + 1;
                addSinglePresentCardDto(rootCard);
                if (noPresentContainerForMovingCard) {
                    addContainer(rootCard.getContainerNo(), rootCard.getRootNo());
                    ContainerAdapter containerAdapter = (ContainerAdapter) containerRecyclerView.getAdapter();
                    if (containerAdapter == null)
                        return;
                    containerAdapter.notifyItemInserted(rootCard.getContainerNo());
                    containerRecyclerViewLayoutManager.setOnRollbackMoveFinishAction(true);
                    containerRecyclerView.smoothScrollToPosition(rootCardContainerPosition);

                    if (hasChildInAllData(rootCard)) {
                        throwToMainHandlerWithDelay(() -> {
                            CardRecyclerView targetCardRecyclerView = findCardRecyclerViewFromContainerRecyclerView(containerRecyclerView, rootCard.getContainerNo());
                            if (targetCardRecyclerView != null)
                                presentChildren(targetCardRecyclerView, rootCard.getContainerNo(), rootCard.getSeqNo());
                        }, 260, containerRecyclerView.getContext());
                    }
                } else {
                    increaseListSeq(nextCardList);
                    containerRecyclerViewLayoutManager.setOnRollbackMoveFinishAction(true);
                    containerRecyclerView.smoothScrollToPosition(rootCardContainerPosition);

                    ((MainCardActivity) containerRecyclerView.getContext()).getMainHandler().postDelayed(() -> {
                        CardRecyclerView targetCardRecyclerView = findCardRecyclerViewFromContainerRecyclerView(containerRecyclerView, rootCard.getContainerNo());
                        if (targetCardRecyclerView == null)
                            return;
                        CardAdapter cardAdapter = targetCardRecyclerView.getAdapter();
                        if (cardAdapter == null)
                            return;
                        cardAdapter.notifyItemInserted(rootCard.getSeqNo());
                        targetCardRecyclerView.smoothScrollToPosition(rootCard.getSeqNo());
//                        presentChildren(targetCardRecyclerView, rootCard.getContainerNo(), rootCard.getSeqNo());
                    }, 400);
                }
            };
            rollbackMovedCardRecyclerViewScrollStatesAboveRootCardContainerPosition(containerRecyclerView, pastOnFocusPositionList, rootCardContainerPosition, afterAboveContainersRollbackAction);
            SingleToastManager.show(SingleToaster.makeTextShort(containerRecyclerView.getContext(), "이동 취소되었습니다."));
        };
    }

    /*
     * exclude rootCardContainerPosition*/
    private void rollbackMovedCardRecyclerViewScrollStatesAboveRootCardContainerPosition(ContainerRecyclerView containerRecyclerView
            , List<Integer> pastOnFocusPositionList, int rootCardContainerPosition, Runnable finishAction) {
        int startRollbackContainerPosition = -1;
        for (int i = 0; i < rootCardContainerPosition; i++) {
            if (getContainer(i).getFocusCardPosition() != pastOnFocusPositionList.get(i)) {
                startRollbackContainerPosition = i;
                break;
            }
        }
        Queue<Pair<Integer, Integer>> rollbackMoveActionFlagQueue = new LinkedList<>();
        for (int i = startRollbackContainerPosition; i < rootCardContainerPosition; i++) {
            if (startRollbackContainerPosition == -1)
                break;
            final int pastOnFocusPosition = pastOnFocusPositionList.get(i);
            rollbackMoveActionFlagQueue.offer(Pair.create(i, pastOnFocusPosition));
//            final int fi = i;

//            replaced code :
//            rollbackActionQueue.offer(() -> {
//                containerRecyclerView.smoothScrollToPosition(fi);
//                CardContainerViewHolder containerViewHolder = (CardContainerViewHolder) containerRecyclerView.findViewHolderForAdapterPosition(fi);
//                if (containerViewHolder == null)
//                    throw new RuntimeException("CardViewModel#rollbackMovedCardRecyclerViewScrollStatesAboveRootCardContainerPosition#containerViewHolder is null");
//                RecyclerView cardRecyclerview = containerViewHolder.getBinding().cardRecyclerview;
//                cardRecyclerview.smoothScrollToPosition(pastOnFocusPositionList.get(fi));
//            });

//            Legacy code :
//            rollbackActionQueue.offer(() -> {
//                containerRecyclerView.smoothScrollToPosition(fi);
//                Runnable scrollCardToTargetPositionAction = () -> {
//                    Logger.hotfixMessage("RUN  / fi : "+fi);
//<Exception>                    ClassCastException : below line
//                    CardContainerViewHolder containerViewHolder = (CardContainerViewHolder) containerRecyclerView.findViewHolderForAdapterPosition(fi);
//                    Logger.hotfixMessage("RUN  / passed no class case exception touched.");
//                    if (containerViewHolder == null)
//                        throw new RuntimeException("CardViewModel#rollbackMovedCardRecyclerViewScrollStatesAboveRootCardContainerPosition#containerViewHolder is null");
//                    RecyclerView cardRecyclerview = containerViewHolder.getBinding().cardRecyclerview;
//                    cardRecyclerview.smoothScrollToPosition(pastOnFocusPositionList.get(fi));
//                };
//                throwToMainHandlerWithDelay(scrollCardToTargetPositionAction, 1300, containerRecyclerView.getContext());
//            });
        }
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerRecyclerViewLayoutManager = containerRecyclerView.getLayoutManager();
        if (containerRecyclerViewLayoutManager == null)
            return;
        containerRecyclerViewLayoutManager.setRollbackMoveActionFlagQueue(rollbackMoveActionFlagQueue);
        containerRecyclerViewLayoutManager.setRollbackMoveFinishAction(finishAction);
        containerRecyclerViewLayoutManager.executeNextRollbackMoveAction();
//        rollbackActionQueue.offer(() -> containerRecyclerView.smoothScrollToPosition(rootCardContainerPosition));
//        ((MainCardActivity) containerRecyclerView.getContext()).scrollActionDelayed(rollbackActionQueue, finishAction);
    }


    public synchronized List<List<Pair<CardDto, CardState>>> getPresentData() {
        return mPresentData;
    }

    private synchronized boolean handleMoveServiceForCardRecyclerView(CardRecyclerView targetView, DragEvent event) {
        CardAdapter cardAdapter = targetView.getAdapter();
        if (cardAdapter == null)
            return false;
        int containerPosition = cardAdapter.getContainerPosition();
        if (containerPosition == -1)
            return false;
        final int screenWidth = DisplayUtil.getScreenWidthAsPixel(targetView.getContext());
        final int MOVE_BOUNDARY_WIDTH = 200;
        CardRecyclerView.ScrollControllableLayoutManager layoutManager = targetView.getLayoutManager();
        if (layoutManager == null)
            return false;

        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) targetView.getParent().getParent();
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerRecyclerViewLayoutManager = containerRecyclerView.getLayoutManager();
        if (containerRecyclerViewLayoutManager == null)
            return false;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_LOCATION:
                final int cardCount_location = getPresentData().get(containerPosition).size();
                Container container_location = mPresentContainerList.get(containerPosition);
                final int focusedCardPosition_location = container_location.getFocusCardPosition();

                if (event.getX() < MOVE_BOUNDARY_WIDTH && focusedCardPosition_location != 0) {
                    layoutManager.smoothScrollToPosition(focusedCardPosition_location - 1);
                    boolean leftCardArrowNeeded = true;
                    if (focusedCardPosition_location - 1 == 0)
                        leftCardArrowNeeded = false;
                    if (leftCardArrowNeeded) {
                        layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_TWO_WAY_ARROW);
                        synchronized (this) {
                            containerRecyclerViewLayoutManager.refreshArrows();
                        }
                    } else {
                        layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_RIGHT_ARROW);
                        synchronized (this) {
                            containerRecyclerViewLayoutManager.refreshArrows();
                        }
                        return true;
                    }
                    if (event.getX() > screenWidth - MOVE_BOUNDARY_WIDTH && focusedCardPosition_location != cardCount_location - 1) {
                        layoutManager.smoothScrollToPosition(focusedCardPosition_location + 1);
                        boolean rightCardArrowNeeded = true;
                        if (focusedCardPosition_location + 1 == cardCount_location - 1)
                            rightCardArrowNeeded = false;
                        if (rightCardArrowNeeded) {
                            layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_TWO_WAY_ARROW);
                            synchronized (this) {
                                containerRecyclerViewLayoutManager.refreshArrows();
                            }
                        } else {
                            layoutManager.showCardArrowsDelayed(CardRecyclerView.ScrollControllableLayoutManager.DIRECTION_LEFT_ARROW);
                            synchronized (this) {
                                containerRecyclerViewLayoutManager.refreshArrows();
                            }
                        }
                    }
                    return true;
                }
                return true;

            case DragEvent.ACTION_DRAG_STARTED:
                return !(mPresentData.size() < containerPosition + 1);
            case DragEvent.ACTION_DRAG_ENTERED:
                if (containerPosition > mPresentData.size() - 1)
                    return false;
                final int cardCount = getPresentData().get(containerPosition).size();
                Container container = mPresentContainerList.get(containerPosition);
                final int focusedCardPosition = container.getFocusCardPosition();

                boolean leftCardArrowNeeded = false;
                boolean rightCardArrowNeeded = false;
                if (focusedCardPosition != 0) {
                    leftCardArrowNeeded = true;
                }
                if (focusedCardPosition < cardCount - 1) {
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


    private boolean hasChildInAllData(CardDto cardDTO) {
        final int rootCardContainerNo = cardDTO.getContainerNo();
        final int rootCardNo = cardDTO.getCardNo();
        if (!(mAllData.size() > rootCardContainerNo + 1)) {
            return false;
        }
        final boolean hasChildList = mAllData.get(rootCardContainerNo + 1).containsKey(rootCardNo);
        if (hasChildList) {
            List<CardDto> foundList = mAllData.get(rootCardContainerNo + 1).get(rootCardNo);
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
            return ((CardContainerViewHolder) viewHolder).getBinding().cardRecyclerViewCardContainerCards;
        return null;
    }

    //Drop target view is empty space view.
    private void dropAndCreateServiceForEmptySpace(RecyclerView containerRecyclerView, int rootCardNo, int targetContainerNo) {
        Logger.message("vm#dropAndCreateService : for empty space");
        SwitchMaterial removeBtn = findRemoveBtnByContainerRecyclerView(containerRecyclerView);
        int removeBtnVisibility = View.INVISIBLE;
        if (removeBtn.isChecked())
            removeBtnVisibility = View.VISIBLE;
        CardDto newCardDto = new CardDto.Builder().rootNo(rootCardNo).containerNo(targetContainerNo).build();
        mCardRepository.insert(newCardDto.toEntity()
                , orderDropDataInsertListenerForEmptySpace(containerRecyclerView, targetContainerNo, removeBtnVisibility));
    }

    private SwitchMaterial findRemoveBtnByContainerRecyclerView(RecyclerView containerRecyclerView) {
        return ((ViewGroup) containerRecyclerView.getParent()).findViewById(R.id.removeSwitch_mainBody_removeSwitch);
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

    private List<CardDto> increaseListCardsSeq(List<Pair<CardDto, CardState>> uiList, int increaseStart) {
        Logger.message("vm#increaseListCardsSeq");
        List<CardDto> result = new ArrayList<>();
        for (int i = increaseStart; i < uiList.size(); i++) {
            Pair<CardDto, CardState> pair = uiList.get(i);
            pair.first.setSeqNo(pair.first.getSeqNo() + 1);
            result.add(pair.first);
        }
        return result;
    }

    private List<CardEntity> dtoListToEntityList(List<CardDto> input) {
        List<CardEntity> result = new ArrayList<>();
        for (CardDto dto : input) {
            result.add(dto.toEntity());
        }
        return result;
    }

    public void loadData(OnDataLoadListener callback) {
        Logger.message("vm#loadData");
        if (!mCardRepository.isDataPrepared()) {
            mCardRepository.readData((lastContainerNo) -> {
                initData(lastContainerNo);
                callback.onLoadData();
            });
        } else {
            if (!mCardRepository.getData().isEmpty())
                initData(mCardRepository.getData().size() - 1);
            callback.onLoadData();
        }
    }

    private void initPresentData(List<HashMap<Integer, List<CardDto>>> dataGroupedByRootNo) {
        mPresentData.clear();
        if (!(mPresentContainerList.size() > 0))
            return;
        if (dataGroupedByRootNo == null || dataGroupedByRootNo.isEmpty())
            return;
        for (int i = 0; i < mPresentContainerList.size(); i++) {
            List<Pair<CardDto, CardState>> foundItemCollector = new ArrayList<>();
            final int testRootNo = mPresentContainerList.get(i).getRootNo();
            List<CardDto> foundDtoList = dataGroupedByRootNo.get(i).get(testRootNo);
            if (foundDtoList == null || foundDtoList.isEmpty())
                return;
            foundDtoList.forEach(dto -> foundItemCollector.add(Pair.create(dto, new CardState())));
            if (!foundItemCollector.isEmpty())
                mPresentData.add(foundItemCollector);
        }
    }

    private void initData(int lastContainerNo) {
        Logger.message("vm#setData");
        List<CardDto> allDTOs = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        List<List<CardDto>> dataGroupedByContainerNo = groupByContainerNo(allDTOs, lastContainerNo + 1);
        List<HashMap<Integer, List<CardDto>>> dataGroupedByRootNo = groupByRootNo(dataGroupedByContainerNo);
        orderBySeqNo(dataGroupedByRootNo);
        initContainerList(dataGroupedByRootNo);
        initPresentData(dataGroupedByRootNo);
        initCardImageMap(allDTOs);
        // for Search func
//        mLiveData.postValue(dataGroupedByContainerNo);
        mAllData.clear();
        mAllData.addAll(dataGroupedByRootNo);
    }

    private void initCardImageMap(List<CardDto> allDtoList) {
        for (CardDto theCardDTO : allDtoList) {
            int theCardNo = theCardDTO.getCardNo();
            cardImageMap.put(theCardNo, new ObservableBitmap());
        }
    }

    private void initContainerList(List<HashMap<Integer, List<CardDto>>> orderedData) {
        if (orderedData == null || orderedData.isEmpty())
            return;
        List<CardDto> firstLayerItems = orderedData.get(0).get(CardDto.NO_ROOT_CARD);
        if (firstLayerItems == null || firstLayerItems.isEmpty())
            return;
        mPresentContainerList.add(new Container(CardDto.NO_ROOT_CARD));
        int rootCardNo = firstLayerItems.get(Container.DEFAULT_CARD_POSITION).getCardNo();
        for (int i = 1; i < orderedData.size(); i++) {
            HashMap<Integer, List<CardDto>> testMap = orderedData.get(i);
            if (testMap.containsKey(rootCardNo)) {
                List<CardDto> testList = testMap.get(rootCardNo);
                if (testList == null || testList.isEmpty())
                    return;
                mPresentContainerList.add(new Container(rootCardNo));
                rootCardNo = testList.get(0).getCardNo();
                continue;
            }
            break;
        }
    }

    private void orderBySeqNo(List<HashMap<Integer, List<CardDto>>> groupedData) {
        for (HashMap<Integer, List<CardDto>> testContainerMap : groupedData) {
            for (int key : testContainerMap.keySet()) {
                List<CardDto> testDtoList = testContainerMap.get(key);
                if (testDtoList != null)
                    Collections.sort(testDtoList);
            }
        }
    }

    private List<HashMap<Integer, List<CardDto>>> groupByRootNo(List<List<CardDto>> dataGroupedByContainerNo) {
        List<HashMap<Integer, List<CardDto>>> result = new ArrayList<>();
        if (dataGroupedByContainerNo == null || dataGroupedByContainerNo.isEmpty())
            return result;
        for (int i = 0; i < dataGroupedByContainerNo.size(); i++) {
            result.add(new HashMap<>());
        }
        final int containerCount = dataGroupedByContainerNo.size();
        for (int i = 0; i < containerCount; i++) {
            List<CardDto> testContainerCardDtoList = dataGroupedByContainerNo.get(i);
            for (CardDto testCardDto : testContainerCardDtoList) {
                final int testRootNo = testCardDto.getRootNo();
                final HashMap<Integer, List<CardDto>> testMap = result.get(i);
                if (!testMap.containsKey(testRootNo))
                    testMap.put(testRootNo, new ArrayList<>());
                Objects.requireNonNull(testMap.get(testRootNo)).add(testCardDto);
            }
        }
        return result;
    }

    private List<List<CardDto>> groupByContainerNo(List<CardDto> data, int containerSize) {
        Logger.message("vm#groupDataByContainerNo");
        List<List<CardDto>> result = new ArrayList<>();
        if (data == null || data.isEmpty())
            return result;
        for (int i = 0; i < containerSize; i++) {
            result.add(new ArrayList<>());
        }
        for (CardDto testDto : data) {
            final int containerPosition = testDto.getContainerNo();
            result.get(containerPosition).add(testDto);
        }
        return result;
    }

    private void resetChildrenPresentData(int rootContainerPosition, int rootCardPosition) {
        Logger.message("vm#resetChildrenPresentData");
        Pair<CardDto, CardState> rootCardPair = mPresentData.get(rootContainerPosition).get(rootCardPosition);
        CardDto rootCardDto = rootCardPair.first;
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
            HashMap<Integer, List<CardDto>> testMap = mAllData.get(rootContainerPosition + 1);
            if (testMap == null || testMap.isEmpty())
                break;
            if (!testMap.containsKey(rootCardNo)) {
                break;
            }
            List<CardDto> foundList = testMap.get(rootCardNo);
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

    private List<Pair<CardDto, CardState>> dtoListToPairList(List<CardDto> dtoList, int removeBtnVisibility) {
        List<Pair<CardDto, CardState>> result = new ArrayList<>();
        if (dtoList == null || dtoList.isEmpty())
            return result;
        for (CardDto dto : dtoList) {
            result.add(Pair.create(dto, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
        }
        return result;
    }

    public void updateCards(List<CardDto> toUpdateCardList, Runnable uiUpdateAction) {
        mCardRepository.update(dtoListToEntityList(toUpdateCardList), uiUpdateAction);
    }

    public void notifySearchQueryTextChanged(String queryText) {
        searchCards(queryText);
        resetFocusPageNo();
        refreshSearchResultPagers();
        getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
    }

    private void refreshSearchResultPagers() {
        // prepared : focusPageNo,
        final int currentPageCount = getCurrentPageCount();
        //setPageCount -> change pagers visibility.
        setPageCount(currentPageCount);

        final int mFocusPageNo = focusPageNo.get();
        final int maxItemPageSetCount = Math.floorDiv(mFocusPageNo, VISIBLE_PAGE_ITEM_MAX_COUNT);
        int baseMaxItemPageSetCount = 0;
        baseMaxItemPageSetCount = maxItemPageSetCount;
        final boolean noRemainderPage = Math.floorMod(mFocusPageNo, VISIBLE_PAGE_ITEM_MAX_COUNT) == 0;
        int basePageCount = 0;
        if (noRemainderPage && baseMaxItemPageSetCount != 0) {
            baseMaxItemPageSetCount--;
        }
//        basePageCount = Math.max(baseMaxItemPageSetCount * VISIBLE_PAGE_ITEM_MAX_COUNT, 0);
        basePageCount = baseMaxItemPageSetCount * VISIBLE_PAGE_ITEM_MAX_COUNT;

        //setLongPagerMode -> change pager constraint attrs.
        setLongPagerMode();

        //set pager text value the page number.
        //note : somewhere do clear.
        for (int i = 0; i < currentPageCount; i++) {
            final int pageNumber = basePageCount + i + 1;
            searchResultPagerTextList.get(i).setValue(String.valueOf(pageNumber));
        }

        //setBackgroundColor colorAccent_c & alpha 1.0f(focus) or 0.2f
        // -> dataBinding
    }

    //note : param basePageCount is (VISIBLE_PAGE_ITEM_MAX_COUNT * n).
    private void setLongPagerMode() {
        boolean currentModeIsLongPageMode = false;
        if (longPagerOn.getValue() != null)
            currentModeIsLongPageMode = longPagerOn.getValue();

        final int currentPageBundleNo = getPageBundleNoByPageNo(getFocusPageNo());
        final int currentFirstPagerNo = (currentPageBundleNo - 1) * VISIBLE_PAGE_ITEM_MAX_COUNT + 1;
        if (pagerCount.getValue() == null)
            return;
        final int currentLastPagerNo = (currentPageBundleNo - 1) * VISIBLE_PAGE_ITEM_MAX_COUNT + pagerCount.getValue();
        boolean hasLongLengthNo = false;
        if (currentFirstPagerNo >= LONG_LENGTH_PAGER_NO || currentLastPagerNo >= LONG_LENGTH_PAGER_NO)
            hasLongLengthNo = true;
        if ((hasLongLengthNo && !currentModeIsLongPageMode)
                || (!hasLongLengthNo && currentModeIsLongPageMode))
            toggleLongPagerOn();

//        ******
//        note : It us target bundle no but target number not contained.
//         ******

//        currentPageBundleNo * VISIBLE_PAGE_ITEM_MAX_COUNT
//        Math.floorDiv(currentPageBundleNo,)
//        final int longPagerModeFlagPageNumber = VISIBLE_PAGE_ITEM_MAX_COUNT;

    }

    private void toggleLongPagerOn() {
        if (longPagerOn.getValue() == null)
            return;
        boolean mode = longPagerOn.getValue();
        longPagerOn.setValue(!mode);
    }


    private void setPageCount(int count) {
        pagerCount.setValue(count);
    }

    public void prepareNextPagers() {
        if (!hasNextPageBundle())
            return;
        int nextPageNo = getPageBundleNoByPageNo(getFocusPageNo()) * VISIBLE_PAGE_ITEM_MAX_COUNT + 1;
        setFocusPageNo(nextPageNo);
        refreshSearchResultPagers();
        getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
    }

    public void preparePrevPagers() {
        if (!hasPrevPageBundle())
            return;
        int prevPageNo = (getPageBundleNoByPageNo(getFocusPageNo()) - 1) * VISIBLE_PAGE_ITEM_MAX_COUNT;
//        int prevPageNo = countBaseMaxPageBundle() * CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT;
        setFocusPageNo(prevPageNo);
        refreshSearchResultPagers();
        getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
    }

    /* Drop Utils*/
    public interface DropDataInsertListener extends Consumer<CardEntity> {
        void accept(CardEntity cardEntity);
    }

    public DropDataInsertListener orderDropDataInsertListenerForEmptySpace(RecyclerView containerRecyclerView, int targetPosition, int removeBtnVisibility) {
        return cardEntity -> {
            mPresentContainerList.add(new Container(cardEntity.getRootNo()));
            mPresentData.add(new ArrayList<>());
            CardDto newCard = cardEntity.toDTO();
            mPresentData.get(targetPosition).add(Pair.create(newCard, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            final boolean noValueInAllData = mAllData.size() < targetPosition + 1;
            if (noValueInAllData) {
                mAllData.add(new HashMap<>());
            }
            HashMap<Integer, List<CardDto>> targetMap = mAllData.get(targetPosition);
            if (!targetMap.containsKey(newCard.getRootNo()))
                targetMap.put(newCard.getRootNo(), new ArrayList<>());
            Objects.requireNonNull(targetMap.get(newCard.getRootNo())).add(newCard);
            addCardImageValue(newCard);
            runOnUiThread(() ->
                    Objects.requireNonNull(containerRecyclerView.getAdapter()).notifyItemInserted(targetPosition), containerRecyclerView.getContext());
        };
    }

    public DropDataInsertListener orderDropDataInsertListenerForContainer(CardState targetCardState, List<Pair<CardDto, CardState>> targetItemList
            , RecyclerView targetRecyclerView) {
        return foundEntity -> {
            Logger.message("DropDataInsertListener#accept");
            final int removeBtnVisibility = targetCardState.getRemoveBtnVisibility();
            CardDto newCard = foundEntity.toDTO();
            targetItemList.add(newCard.getSeqNo(), Pair.create(newCard, new CardState.Builder().removeBtnVisibility(removeBtnVisibility).build()));
            Objects.requireNonNull(mAllData.get(newCard.getContainerNo()).get(newCard.getRootNo())).add(newCard.getSeqNo(), newCard);
            addCardImageValue(newCard);
            runOnUiThread(() -> {
                Logger.message("runOnUiThread");
                Objects.requireNonNull(targetRecyclerView.getAdapter()).notifyItemInserted(newCard.getSeqNo());
                targetRecyclerView.scrollToPosition(newCard.getSeqNo());
                mPresentContainerList.get(newCard.getContainerNo()).setFocusCardPosition(newCard.getSeqNo());
                presentChildren(targetRecyclerView, newCard.getContainerNo(), newCard.getSeqNo());
            }, targetRecyclerView.getContext());
        };
    }

    /* Container Level */

    // +1: For empty card space.
    public int presentContainerCount() {
        final int EMPTY_CARD_SPACE_COUNT = 1;
        return mPresentData.size() + EMPTY_CARD_SPACE_COUNT;
    }

    /* Card Level */
    public synchronized int getPresentCardCount(int containerPosition) {
        Logger.message("vm#getPresentCardCount");
        if (containerPosition == -1)
            return 0;
        if (mPresentData.size() < containerPosition + 1)
            return 0;
        return mPresentData.get(containerPosition).size();
    }

    //sync with allData
    public ObservableBitmap getCardImage(int containerPosition, int cardPosition) {
        int cardNo = getCardDto(containerPosition, cardPosition).getCardNo();
        return cardImageMap.get(cardNo);
    }

    public CardDto getCardDto(int containerPosition, int cardPosition) {
        Logger.message("vm#getCardDto");
        return mPresentData.get(containerPosition).get(cardPosition).first;
    }

    public CardState getCardState(int containerPosition, int cardPosition) {
        Logger.message("vm#getCardState");
        return mPresentData.get(containerPosition).get(cardPosition).second;
    }

    public void updateCard(CardDto cardDto) {
        Logger.message("vm#updateCard");
        mCardRepository.update(cardDto.toEntity());
    }

    public RecyclerView.OnScrollListener getOnScrollListenerForContainerRecyclerView() {
        return mContainerRecyclerViewScrollListener;
    }

    @BindingAdapter("onScrollListener")
    public static void setOnScrollListener(RecyclerView view, RecyclerView.OnScrollListener scrollListener) {
        view.addOnScrollListener(scrollListener);
    }

    @BindingAdapter("onQueryTextListener")
    public static void setOnQueryTextListener(SearchView view, SearchView.OnQueryTextListener listener) {
        view.setOnQueryTextListener(listener);
    }

    @BindingAdapter("onDragListener")
    public static void setOnDragListener(View view, View.OnDragListener listener) {
        view.setOnDragListener(listener);
    }

    public SearchView.OnQueryTextListener getOnQueryTextListenerForSearchingCard() {
        return onQueryTextListenerForSearchingCard;
    }

    public View.OnDragListener getOnDragListenerForTopArrow() {
        return onDragListenerForTopArrow;
    }

    public View.OnDragListener getOnDragListenerForBottomArrow() {
        return onDragListenerForBottomArrow;
    }

    @BindingAdapter("onLongClickListener")
    public static void setOnLongClickListener(View view, View.OnLongClickListener listener) {
        view.setOnLongClickListener(listener);
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

    public synchronized void presentChildren(RecyclerView cardRecyclerView, int rootContainerPosition, int rootCardPosition) {
        final int prevPresentContainerSize = mPresentContainerList.size();
        resetPresentContainerList(rootContainerPosition, rootCardPosition);
        resetChildrenPresentData(rootContainerPosition, rootCardPosition);
        notifyContainerItemChanged(((RecyclerView) cardRecyclerView.getParent().getParent()), getContainerAdapterFromCardRecyclerView(cardRecyclerView)
                , prevPresentContainerSize, mPresentData.size()
                , rootContainerPosition);
    }


    private void resetPresentContainerList(int rootContainerPosition, int rootCardPosition) {
        final int prevLastPosition = mPresentContainerList.size() - 1;
        if (prevLastPosition > rootContainerPosition) {
            mPresentContainerList.subList(rootContainerPosition + 1, prevLastPosition + 1).clear();
        }

        boolean hasNextContainer = mAllData.size() > rootContainerPosition + 1;
        CardDto rootCard = mPresentData.get(rootContainerPosition).get(rootCardPosition).first;
        while (hasNextContainer) {
            final int rootCardNo = rootCard.getCardNo();
            List<CardDto> testList = mAllData.get(rootContainerPosition + 1).get(rootCardNo);
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
        RecyclerView cardRecyclerView = getCardRecyclerViewFromRemoveButton(view);
        ConstraintLayout containerFrame = (ConstraintLayout) cardRecyclerView.getParent();
        return (RecyclerView) containerFrame.getParent();
    }

    private RecyclerView getCardRecyclerViewFromRemoveButton(View view) {
        FrameLayout removeBtnFrame = (FrameLayout) view.getParent();
        ConstraintLayout cardFrame = (ConstraintLayout) removeBtnFrame.getParent();
        return (RecyclerView) cardFrame.getParent();
    }
}