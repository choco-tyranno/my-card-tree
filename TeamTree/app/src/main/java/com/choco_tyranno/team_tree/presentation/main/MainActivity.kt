package com.choco_tyranno.team_tree.presentation.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.databinding.ActivityMainBinding
import com.choco_tyranno.team_tree.domain.card_data.CardDto
import com.choco_tyranno.team_tree.presentation.CardViewModel
import com.choco_tyranno.team_tree.presentation.DependentUIResolver.DependentUIResolverBuilder
import com.choco_tyranno.team_tree.presentation.SingleToastManager
import com.choco_tyranno.team_tree.presentation.SingleToaster
import com.choco_tyranno.team_tree.presentation.card_rv.CardGestureListener
import com.choco_tyranno.team_tree.presentation.card_rv.CardTouchListener
import com.choco_tyranno.team_tree.presentation.card_rv.CardViewShadowProvider
import com.choco_tyranno.team_tree.presentation.card_rv.listener.OnClickListenerForCallBtn
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerAdapter
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerRecyclerView
import com.choco_tyranno.team_tree.presentation.searching_drawer.CardFinder
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.lang.RuntimeException
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val cardViewModel: CardViewModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardFounder : CardFinder

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
        binding.lifecycleOwner = this@MainActivity
        cardFounder = CardFinder(this@MainActivity)
    }

    private fun loadDefaultImageResource() {
        if (cardViewModel.hasDefaultCardImage())
            return
        Thread() {
            val width = resources.getDimension(R.dimen.card_thumbnail_image_width).roundToInt()
            val height = resources.getDimension(R.dimen.card_thumbnail_image_height).roundToInt()
            Glide.with(this@MainActivity).asBitmap()
                .load(R.drawable.default_card_image_01)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        SingleToaster.makeTextLong(
                            this@MainActivity,
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
        containerRv.adapter = ContainerAdapter(this@MainActivity)
        containerRv.setLayoutManager(
            ContainerRecyclerView
                .ItemScrollingControlLayoutManager(
                    this@MainActivity, LinearLayoutManager.VERTICAL, false
                )
        )
        containerRv.adapter?.notifyDataSetChanged()
    }

    private fun initSearchResultRv() {
        binding.layoutSearchdrawer.cardSearchResultRecyclerview
            .addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity, DividerItemDecoration.VERTICAL
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

        val newCardButton = binding.layoutMainbody.buttonMainBodyNewCard
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

    private fun initUiUtils(){
        val cardGestureListener = CardGestureListener()
        val cardGestureDetector = GestureDetectorCompat(this@MainActivity, cardGestureListener)
        cardViewModel.setCardGestureListener(cardGestureListener)
        cardViewModel.setCardGestureDetector(cardGestureDetector)
        cardViewModel.connectGestureUtilsToOnCardTouchListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContainerView(){
        runOnUiThread { binding.layoutMainbody.containerRecyclerViewMainBodyContainers
            .adapter?.notifyDataSetChanged()}
    }

    private fun waitToLoadDefaultImageResource(){
        if (cardViewModel.hasDefaultCardImage()){
            showContainerView()
            return
        }
        handler.postDelayed(Runnable { waitToLoadDefaultImageResource()
        },500)
    }

    private fun loadAllCardResources(){
        if (cardViewModel.isDataInitialized)
            return
        val allCardArr = cardViewModel.pictureCardArr
        val width = resources.getDimension(R.dimen.card_thumbnail_image_width).roundToInt()
        val height = resources.getDimension(R.dimen.card_thumbnail_image_height).roundToInt()
        for (card in allCardArr){
            if (TextUtils.equals(card.imagePath, "")) continue
            handler.post {
                val cardNo: Int = card.cardNo
                try {
                    Glide.with(this@MainActivity).asBitmap()
                        .load(card.imagePath)
                        .addListener(object : RequestListener<Bitmap?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any,
                                target: Target<Bitmap?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                SingleToaster.makeTextShort(this@MainActivity
                                    ,"[${card.title}] 이미지를 불러오는데 실패했습니다. 다시 시도해주세요.").show()
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
        setContentView(R.layout.testactivity_main)
        initVarWithContext()
        binding.viewModel = cardViewModel
        loadDefaultImageResource()
        initContentView()
        initUiUtils()
        cardViewModel.loadData(){
            waitToLoadDefaultImageResource()
            loadAllCardResources()
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
        CardViewShadowProvider.onDestroy();
    }

    companion object {
        const val TAG = "@@MainActivity"
    }
}