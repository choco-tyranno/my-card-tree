package com.choco_tyranno.team_tree.ui.detail_page;

import android.app.Application;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;

import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.domain.source.CardRepository;

public class DetailPageViewModel extends AndroidViewModel {
    private CardRepository repository;
    private View.OnClickListener onClickListenerForModeSwitchBtn;
    private View.OnClickListener onClickListenerForBackBtn;
    private View.OnClickListener onClickListenerForSaveBtn;
    private View.OnClickListener onClickListenerForUtilContainerFab;
    private View.OnClickListener onClickListenerForTakePictureFab;
    private View.OnClickListener onClickListenerForOpenGalleryFab;
    private View.OnClickListener onClickListenerForLoadContactInfoFab;
    private View.OnClickListener onClickListenerForSpreadingImage;
    private DetailPage detailPage;
    private Bitmap defaultCardImage;

    public void setDefaultCardImage(Bitmap resource){
        defaultCardImage = resource;
    }

    public Bitmap getDefaultCardImage(){
        return defaultCardImage;
    }

    @BindingAdapter(value = {"cardImage", "defaultCardImage"})
    public static void setCardImage(ImageView view, Bitmap pictureCardImage, Bitmap defaultCardImage) {
        if (pictureCardImage != null)
            view.setImageBitmap(pictureCardImage);
        else
            view.setImageBitmap(defaultCardImage);
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
        onClickListenerForSpreadingImage = new OnClickListenerForSpreadingImage();
    }

    public View.OnClickListener getOnClickListenerForSpreadingImage() {
        return onClickListenerForSpreadingImage;
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

    public void update(CardDto cardDTO) {
        repository.update(cardDTO.toEntity());
    }
}
