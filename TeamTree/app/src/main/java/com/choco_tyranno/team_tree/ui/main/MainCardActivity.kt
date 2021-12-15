package com.choco_tyranno.team_tree.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.databinding.ActivityMainBinding
import com.choco_tyranno.team_tree.domain.card_data.CardDto
import com.choco_tyranno.team_tree.ui.CardViewModel
import com.choco_tyranno.team_tree.ui.DependentUIResolver.DependentUIResolverBuilder
import com.choco_tyranno.team_tree.ui.SingleToastManager
import com.choco_tyranno.team_tree.ui.SingleToaster
import com.choco_tyranno.team_tree.ui.card_rv.CardGestureListener
import com.choco_tyranno.team_tree.ui.card_rv.CardViewShadowProvider
import com.choco_tyranno.team_tree.ui.container_rv.CardContainerViewHolder
import com.choco_tyranno.team_tree.ui.container_rv.ContainerAdapter
import com.choco_tyranno.team_tree.ui.container_rv.ContainerRecyclerView
import com.choco_tyranno.team_tree.ui.searching_drawer.CardFinder
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*
import kotlin.math.roundToInt

class MainCardActivity : AppCompatActivity() {
    val cardViewModel: CardViewModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardFinder: CardFinder
    private val activityResultLauncherForDetailScene = createActivityResultLauncherForDetailScene()
    fun getActivityResultLauncherForDetailScene() = activityResultLauncherForDetailScene

    private fun createActivityResultLauncherForDetailScene(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode != RESULT_OK){
                SingleToaster.makeTextShort(this@MainCardActivity,resources.getString(R.string.main_detailActivityResultNotOk)).show()
                return@registerForActivityResult
            }
            val updatedCardDto = result.data?.getSerializableExtra("post_card") as CardDto
            val imageChanged: Boolean = cardViewModel.isCardImageChanged(updatedCardDto)
            cardViewModel.applyCardFromDetailActivity(updatedCardDto)
            if (imageChanged) {
                loadNewCardImage(updatedCardDto)
            }
        }
    }

    private fun scrollActionDelayed(scrollActionQueue: Queue<Runnable>, finishAction: Runnable?) {
        handler.postDelayed(Runnable {
            if (scrollActionQueue.isEmpty()) {
                finishAction?.run()
                return@Runnable
            }
            scrollActionQueue.poll()?.run()
            scrollActionDelayed(scrollActionQueue, finishAction)
        }, 900)
    }

    fun scrollToFindingTargetCard(
        scrollUtilDataForFindingOutCard: Pair<Int, Array<Int>>,
        finishAction: Runnable
    ) {
        val startContainerPosition = scrollUtilDataForFindingOutCard.first
        val scrollTargetCardSeqArr: Array<Int> = scrollUtilDataForFindingOutCard.second
        val containerRecyclerview: RecyclerView =
            binding.layoutMainbody.containerRecyclerViewMainBodyContainers
        val scrollActionQueue: Queue<Runnable> = LinkedList()
        for ((index, targetContainerNo) in (startContainerPosition until startContainerPosition + scrollTargetCardSeqArr.size).withIndex()) {
            scrollActionQueue.offer(Runnable {
                containerRecyclerview.smoothScrollToPosition(targetContainerNo)
                val delayedAction = Runnable {
                    val containerViewHolder =
                        containerRecyclerview.findViewHolderForAdapterPosition(targetContainerNo) as CardContainerViewHolder
                    val cardRecyclerview: RecyclerView =
                        containerViewHolder.binding.cardRecyclerViewCardContainerCards
                    cardRecyclerview.smoothScrollToPosition(scrollTargetCardSeqArr[index])
                }
                handler.postDelayed(delayedAction, 900)
            })
        }
        if (scrollActionQueue.isEmpty()) {
            handler.postDelayed(Runnable {
                containerRecyclerview.smoothScrollToPosition(
                    startContainerPosition
                )
            }, 900)
            finishAction.run()
            return
        }
        scrollActionDelayed(scrollActionQueue, finishAction)
    }

    fun getCardFinder() = cardFinder

    fun getMainHandler() = handler

    fun getBinding(): ActivityMainBinding {
        if (::binding.isInitialized)
            return binding
        else
            throw RuntimeException("MainCardActivity/tried : getBinding()/ BindingIsNotInitialized")
    }

    //Context is already with the lifecycle method #onCreate.
    private fun initVarWithContext() {
        handler = Handler(mainLooper)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this@MainCardActivity
        cardFinder = CardFinder(this@MainCardActivity)
    }

    private fun loadNewCardImage(updatedCardDto: CardDto) {
        if (TextUtils.equals(updatedCardDto.imagePath, "")) {
            cardViewModel.setCardImageResource(null, updatedCardDto.cardNo)
            return
        }
        handler.post(Runnable {
            val cardNo: Int = updatedCardDto.cardNo
            val width =
                resources.getDimension(R.dimen.card_thumbnail_image_width).roundToInt()
            val height =
                resources.getDimension(R.dimen.card_thumbnail_image_height).roundToInt()
            Glide.with(this@MainCardActivity).asBitmap()
                .load(updatedCardDto.imagePath)
                .addListener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Bitmap?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any,
                        target: Target<Bitmap?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        cardViewModel.setCardImageResource(resource, cardNo)
                        return false
                    }
                }).submit(width, height)
        })
    }

    private fun loadDefaultImageResource() {
        if (cardViewModel.hasDefaultCardImage())
            return
        Thread() {
            val width = resources.getDimension(R.dimen.card_thumbnail_image_width).roundToInt()
            val height = resources.getDimension(R.dimen.card_thumbnail_image_height).roundToInt()
            Glide.with(this@MainCardActivity).asBitmap()
                .load(R.drawable.default_card_image_01)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        SingleToaster.makeTextLong(
                            this@MainCardActivity,
                            "기본카드 이미지를 불어올 수 없습니다. 앱을 재시작해주세요."
                        ).show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        cardViewModel.setDefaultCardImage(resource)
                        return false
                    }
                }).submit(width, height)
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initContainerRv() {
        val containerRv: ContainerRecyclerView =
            binding.layoutMainbody.containerRecyclerViewMainBodyContainers
        containerRv.adapter = ContainerAdapter(this@MainCardActivity)
        containerRv.setLayoutManager(
            ContainerRecyclerView
                .ItemScrollingControlLayoutManager(
                    this@MainCardActivity, LinearLayoutManager.VERTICAL, false
                )
        )
        containerRv.adapter?.notifyDataSetChanged()
    }

    private fun initSearchResultRv() {
        binding.layoutSearchdrawer.cardSearchResultRecyclerview
            .addItemDecoration(
                DividerItemDecoration(
                    this@MainCardActivity, DividerItemDecoration.VERTICAL
                )
            )
    }

    private fun initContentView() {
        setSupportActionBar(binding.layoutMainbody.toolbarMainBodyDefaultAppBar)
        supportActionBar?.hide()

        val topAppBar = binding.layoutMainbody.viewMainBodyTopAppBar
        DependentUIResolverBuilder<View>()
            .baseView(topAppBar)
            .with(
                topAppBar.id,
                binding.layoutMainbody.removeSwitchMainBodyRemoveSwitch::setScaleByTopAppBar,
                binding.layoutSearchdrawer.cardSearchView::setConstrainFixedHeightByTopAppBar
            ).build()
            .resolve()

        val newCardButton = binding.layoutMainbody.newCardViewMainBodyNewCard
        DependentUIResolverBuilder<View>()
            .baseView(newCardButton)
            .with(
                newCardButton.id,
                binding.layoutMainbody.bottomBarMainBodyBottomBar::setHeightByNewCardButton
            ).build()
            .resolve()

        initContainerRv()
        initSearchResultRv()
        val searchBtn: ImageView =
            binding.layoutSearchdrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_button)
        val searchCloseBtn: ImageView =
            binding.layoutSearchdrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        val searchAutoComplete: SearchAutoComplete =
            binding.layoutSearchdrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchBtn.setColorFilter(R.color.colorPrimary_a)
        searchCloseBtn.setColorFilter(R.color.colorPrimary_a)
        searchAutoComplete.setTextColor(resources.getColor(R.color.colorPrimary, theme))
    }

    private fun initUiUtils() {
        val cardGestureListener = CardGestureListener()
        val cardGestureDetector = GestureDetectorCompat(this@MainCardActivity, cardGestureListener)
        cardViewModel.setCardGestureListener(cardGestureListener)
        cardViewModel.setCardGestureDetector(cardGestureDetector)
        cardViewModel.connectGestureUtilsToOnCardTouchListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContainerView() {
        runOnUiThread {
            binding.layoutMainbody.containerRecyclerViewMainBodyContainers
                .adapter?.notifyDataSetChanged()
        }
    }

    private fun waitToLoadDefaultImageResource() {
        if (cardViewModel.hasDefaultCardImage()) {
            showContainerView()
            return
        }
        handler.postDelayed(Runnable {
            waitToLoadDefaultImageResource()
        }, 500)
    }

    private fun loadAllCardImageResources() {
        if (cardViewModel.isDataInitialized)
            return
        val allCardArr = cardViewModel.pictureCardArr
        val width = resources.getDimension(R.dimen.card_thumbnail_image_width).roundToInt()
        val height = resources.getDimension(R.dimen.card_thumbnail_image_height).roundToInt()
        for (card in allCardArr) {
            if (TextUtils.equals(card.imagePath, "")) continue
            handler.post {
                val cardNo: Int = card.cardNo
                try {
                    Glide.with(this@MainCardActivity).asBitmap()
                        .load(card.imagePath)
                        .addListener(object : RequestListener<Bitmap?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any,
                                target: Target<Bitmap?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                SingleToaster.makeTextShort(
                                    this@MainCardActivity,
                                    "[${card.title}] 이미지를 불러오는데 실패했습니다. 다시 시도해주세요."
                                ).show()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any,
                                target: Target<Bitmap?>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                cardViewModel.setCardImageResource(resource, cardNo)
                                return false
                            }
                        }).submit(width, height)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        cardViewModel.setDataInitialized()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVarWithContext()
        binding.viewModel = cardViewModel
        loadDefaultImageResource()
        initContentView()
        initUiUtils()
        cardViewModel.loadData() {
            waitToLoadDefaultImageResource()
            loadAllCardImageResources()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        SingleToastManager.clear()
        CardViewShadowProvider.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        supportActionBar?.hide()
        cardViewModel.toggleSettingsOn()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val mainDL = binding.drawerLayoutMainSearchDrawer
        if (mainDL.isDrawerOpen(GravityCompat.END)) {
            mainDL.closeDrawer(GravityCompat.END)
            val searchView: SearchView = binding.layoutSearchdrawer.cardSearchView
            searchView.setQuery("", false)
            searchView.isIconified = true
            cardFinder.isSendingFindCardReq = false
            return
        }
        if (cardViewModel.isSettingsOn.value == true) {
            cardViewModel.toggleSettingsOn()
            supportActionBar?.hide()
        } else super.onBackPressed()
    }

    companion object {
        const val TAG = "@@MainActivity"
        fun test() {}
    }
}