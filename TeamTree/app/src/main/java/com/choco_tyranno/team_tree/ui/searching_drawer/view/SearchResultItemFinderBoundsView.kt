package com.choco_tyranno.team_tree.ui.searching_drawer.view

import android.content.Context
import android.util.AttributeSet
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.choco_tyranno.team_tree.databinding.ItemSearchresultBinding
import com.choco_tyranno.team_tree.domain.card_data.CardDto
import com.choco_tyranno.team_tree.ui.main.MainCardActivity
import com.choco_tyranno.team_tree.ui.searching_drawer.SearchingResultAdapter.SearchingResultViewHolder

class SearchResultItemFinderBoundsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    init {
        setOnClickListener(OnClickListenerForSearchResultItemFinderBoundsView.getInstance())
    }

    private class OnClickListenerForSearchResultItemFinderBoundsView private constructor() :
        OnClickListener {
        override fun onClick(v: View) = findOutTarget(v)
        private fun findOutTarget(v: View) {
            val mainCardActivity = v.context as MainCardActivity
            val cardFinder = mainCardActivity.getCardFinder()
            val viewModel = mainCardActivity.cardViewModel
            if (cardFinder.isFindCardRequested) return
            cardFinder.animate(v)
            val parentView = v.parent as View
            val resultRecyclerView = parentView.parent as RecyclerView
            val targetPosition = resultRecyclerView.getChildAdapterPosition(parentView)
            val searchingResultViewHolder =
                resultRecyclerView.findViewHolderForAdapterPosition(targetPosition) as SearchingResultViewHolder?
                    ?: return
            val binding: ItemSearchresultBinding = searchingResultViewHolder.binding
            val cardDTO: CardDto = binding.card
                ?: return
            var scrollUtilDataForFindingOutCard =
                viewModel.findScrollUtilDataForFindingOutCard(cardDTO)
            if (scrollUtilDataForFindingOutCard.second.isEmpty()) scrollUtilDataForFindingOutCard =
                Pair.create(cardDTO.containerNo, scrollUtilDataForFindingOutCard.second)
            mainCardActivity.scrollToFindingTargetCard(scrollUtilDataForFindingOutCard) {
                cardFinder.isFindCardRequested = false
            }
        }

        companion object {
            private val instance: OnClickListenerForSearchResultItemFinderBoundsView =
                OnClickListenerForSearchResultItemFinderBoundsView()

            @JvmStatic
            fun getInstance() = instance
        }
    }
}