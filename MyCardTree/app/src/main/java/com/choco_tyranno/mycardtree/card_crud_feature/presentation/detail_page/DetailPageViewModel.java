package com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page;

import android.app.Application;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;

public class DetailPageViewModel extends AndroidViewModel {
    private CardRepository repository;
    private View.OnClickListener onClickListenerForModeSwitchBtn;
    private View.OnClickListener onClickListenerForBackBtn;
    private View.OnClickListener onClickListenerForSaveBtn;
    private View.OnClickListener onClickListenerForUtilContainerFab;
    private View.OnClickListener onClickListenerForTakePictureFab;
    private View.OnClickListener onClickListenerForOpenGalleryFab;
    private View.OnClickListener onClickListenerForLoadContactInfoFab;
    private DetailPage detailPage;


    @BindingAdapter("cardImage")
    public static void setCardImage(ImageView view, Bitmap resource){
        view.setImageBitmap(resource);
    }

    public DetailPageViewModel(@NonNull Application application) {
        super(application);
        this.repository = CardRepository.getInstance();
    }

    public void setDetailPage(DetailPage detailPage) {
        this.detailPage = detailPage;
    }

    public void initListeners() {
        this.onClickListenerForModeSwitchBtn = new OnClickListenerForModeSwitchBtn(detailPage);
        this.onClickListenerForBackBtn = new OnClickListenerForBackBtn();
        this.onClickListenerForSaveBtn = new OnClickListenerForSaveBtn(this);
        onClickListenerForUtilContainerFab = new OnClickListenerForUtilContainerFab();
        onClickListenerForTakePictureFab = new OnClickListenerForTakePictureFab();
        onClickListenerForOpenGalleryFab = new OnClickListenerForOpenGalleryFab();
        onClickListenerForLoadContactInfoFab = new OnClickListenerForLoadContactInfoFab();
    }

    public View.OnClickListener getOnClickListenerForModeSwitchBtn() {
        return onClickListenerForModeSwitchBtn;
    }

    public View.OnClickListener getOnClickListenerForBackBtn() {
        return onClickListenerForBackBtn;
    }

    public View.OnClickListener getOnClickListenerForSaveBtn() {
        return onClickListenerForSaveBtn;
    }

    public View.OnClickListener getOnClickListenerForUtilContainerFab() {
        return onClickListenerForUtilContainerFab;
    }
    public View.OnClickListener getOnClickListenerForTakePictureFab() {
        return onClickListenerForTakePictureFab;
    }
    public View.OnClickListener getOnClickListenerForOpenGalleryFab() {
        return onClickListenerForOpenGalleryFab;
    }
    public View.OnClickListener getOnClickListenerForLoadContactInfoFab() {
        return onClickListenerForLoadContactInfoFab;
    }

    public void update(CardDTO cardDTO) {
        repository.update(cardDTO.toEntity());
    }
}
