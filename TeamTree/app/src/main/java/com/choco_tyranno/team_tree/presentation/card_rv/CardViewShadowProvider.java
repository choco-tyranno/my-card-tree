package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;

import java.util.Optional;

import javax.annotation.Nullable;

public class CardViewShadowProvider {
    private static LazyHolder holder;
    private View cloneCardViewInstance;

    private CardViewShadowProvider() {

    }

    public static void onDestroy() {
        if (holder != null)
            holder = null;
    }

    public static View getInstance(Context context,@Nullable CardDto cardDto) {
        if (holder == null) {
            holder = new LazyHolder();
        }
        if (!Optional.ofNullable(holder.get().cloneCardViewInstance).isPresent()) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder.get().cloneCardViewInstance = layoutInflater.inflate(R.layout.item_card_front_clone, null, false);
            holder.get().cloneCardViewInstance.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            holder.get().cloneCardViewInstance.layout(0, 0, (int) context.getResources().getDimension(R.dimen.cloneCard_width), (int) context.getResources().getDimension(R.dimen.cloneCard_height));
        }
        View cardShadowView = holder.get().cloneCardViewInstance;

        TextView title = cardShadowView.findViewById(R.id.cloneFrontCard_titleTextView);
       TextView contactNumber = cardShadowView.findViewById(R.id.cloneFrontCard_contactNumberTextView);
        if (cardDto != null) {
            title.setText(cardDto.getTitle());
            contactNumber.setText(cardDto.getContactNumber());
        }else {
            title.setText("");
            contactNumber.setText("");
        }
        return holder.get().cloneCardViewInstance;
    }

    public static class LazyHolder {
        private final CardViewShadowProvider mProvider;

        public LazyHolder() {
            mProvider = new CardViewShadowProvider();
        }

        public CardViewShadowProvider get() {
            return mProvider;
        }
    }
}
