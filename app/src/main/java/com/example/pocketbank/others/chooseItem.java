package com.example.pocketbank.others;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.example.pocketbank.R;
import com.example.pocketbank.adapters.autoCompleteTextViewAdapter;
import com.example.pocketbank.adapters.itemAdapter;
import com.example.pocketbank.databinding.ActivityChooseItemBinding;
import com.example.pocketbank.model.Item;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;

public class chooseItem extends AppCompatActivity
{
    public MaterialTextView findItemText;
    public ActivityChooseItemBinding activityChooseItemBinding;
    public boolean itemShopped ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(1000);
        activityChooseItemBinding = ActivityChooseItemBinding.inflate(getLayoutInflater());
        setContentView(activityChooseItemBinding.getRoot());
        activityChooseItemBinding.getRoot().setLayoutTransition(layoutTransition);
        initializeViews();
    }

    private void initializeViews()
    {
        findItemText = activityChooseItemBinding.findItemText;
        ArrayList<Integer> photoResIds = new ArrayList<>();
        ArrayList<Integer> nameOfItems = new ArrayList<>();
        photoResIds.add(R.drawable.vegetables_100);
        photoResIds.add(R.drawable.fruit_100);
        photoResIds.add(R.drawable.ic_medicine_100);
        photoResIds.add(R.drawable.ic_petproducts_100);
        photoResIds.add(R.drawable.fashion_100);
        photoResIds.add(R.drawable.grooming_100);
        photoResIds.add(R.drawable.books_100);
        photoResIds.add(R.drawable.toys_100);
        photoResIds.add(R.drawable.cookingoil_100);
        photoResIds.add(R.drawable.milk_100);
        photoResIds.add(R.drawable.meat_100);
        photoResIds.add(R.drawable.snacks_100);
        photoResIds.add(R.drawable.ayurvedic_100);
        photoResIds.add(R.drawable.ic_homeappliances_100);
        photoResIds.add(R.drawable.ic_homegroceries_100);
        photoResIds.add(R.drawable.construction_tool_100);
        photoResIds.add(R.drawable.fitness_100);
        photoResIds.add(R.drawable.jewel_100);

        nameOfItems.add(R.string.vegetables);
        nameOfItems.add(R.string.fruits);
        nameOfItems.add(R.string.medicines);
        nameOfItems.add(R.string.petProducts);
        nameOfItems.add(R.string.fashion);
        nameOfItems.add(R.string.grooming);
        nameOfItems.add(R.string.books);
        nameOfItems.add(R.string.toys);
        nameOfItems.add(R.string.cookingOil);
        nameOfItems.add(R.string.milkProducts);
        nameOfItems.add(R.string.meat);
        nameOfItems.add(R.string.snacks);
        nameOfItems.add(R.string.ayurVedicProducts);
        nameOfItems.add(R.string.Appliances);
        nameOfItems.add(R.string.Groceries);
        nameOfItems.add(R.string.tools);
        nameOfItems.add(R.string.fitness);
        nameOfItems.add(R.string.jewellery);

        activityChooseItemBinding.searchDifferentBackground.setOnClickListener( v ->
        {
            activityChooseItemBinding.searchBoxDifferentBackground.setVisibility(View.GONE);
            activityChooseItemBinding.searchQuery.setVisibility(View.VISIBLE);

        });

        activityChooseItemBinding.search.setOnClickListener(v ->
        {
            activityChooseItemBinding.doneButton.setVisibility(View.GONE);
            activityChooseItemBinding.searchBox.setVisibility(View.GONE);
            activityChooseItemBinding.searchText.setVisibility(View.GONE);
            activityChooseItemBinding.smallSearchText.setVisibility(View.GONE);
            activityChooseItemBinding.items.setVisibility(View.GONE);
            activityChooseItemBinding.searchQuery.setVisibility(View.VISIBLE);

            if(activityChooseItemBinding.searchQuery.requestFocus())
            {
                readyAutoCompleteTextViewAdapter(activityChooseItemBinding);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                activityChooseItemBinding.findItemText.setVisibility(View.VISIBLE);
            }

        });

        activityChooseItemBinding.searchQuery.setStartIconOnClickListener( v ->
        {
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activityChooseItemBinding.searchQuery.getWindowToken(),0);
            activityChooseItemBinding.searchQuery.setVisibility(View.GONE);
            activityChooseItemBinding.findItemText.setVisibility(View.GONE);
            activityChooseItemBinding.searchText.setVisibility(View.VISIBLE);
            activityChooseItemBinding.searchBox.setVisibility(View.VISIBLE);
            activityChooseItemBinding.items.setVisibility(View.VISIBLE);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3, RecyclerView.VERTICAL,false);

        activityChooseItemBinding.items.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0)
                {

                    ((ConstraintLayout.LayoutParams)activityChooseItemBinding.searchBox.getLayoutParams()).topToBottom = R.id.smallSearchText;
                    activityChooseItemBinding.searchText.setVisibility(View.GONE);
                    activityChooseItemBinding.smallSearchText.setVisibility(View.VISIBLE);

                }
                else if(dy < 0)
                {

                    ((ConstraintLayout.LayoutParams)activityChooseItemBinding.searchBox.getLayoutParams()).topToBottom = R.id.searchText;
                    activityChooseItemBinding.smallSearchText.setVisibility(View.GONE);
                    activityChooseItemBinding.searchText.setVisibility(View.VISIBLE);

                }

            }

        });

        activityChooseItemBinding.items.setLayoutManager(gridLayoutManager);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            activityChooseItemBinding.items.setAdapter(new itemAdapter( photoResIds, nameOfItems ,getSystemService(Context.VIBRATOR_SERVICE),activityChooseItemBinding.chooseItemActivityParentLayout, this));
        else
            activityChooseItemBinding.items.setAdapter(new itemAdapter(photoResIds ,nameOfItems , getSystemService(Context.VIBRATOR_MANAGER_SERVICE),activityChooseItemBinding.chooseItemActivityParentLayout, this ));

    }

    private void readyAutoCompleteTextViewAdapter(ActivityChooseItemBinding binding) {
        ArrayList<Item> items = new ArrayList<>();
        Item item = new Item(R.string.petProducts, R.drawable.ic_petproducts_100);
        items.add(item);
        Item item1 = new Item(R.string.medicines, R.drawable.ic_medicine_100);
        items.add(item1);
        Item item2 = new Item(R.string.books, R.drawable.books_100);
        items.add(item2);
        Item item3 = new Item(R.string.ayurVedicProducts, R.drawable.ayurvedic_100);
        items.add(item3);
        Item item4 = new Item(R.string.fitness, R.drawable.fitness_100);
        items.add(item4);
        Item item5 = new Item(R.string.snacks, R.drawable.snacks_100);
        items.add(item5);
        Item item6 = new Item(R.string.milkProducts, R.drawable.milk_100);
        items.add(item6);
        Item item7 = new Item(R.string.vegetables, R.drawable.vegetables_100);
        items.add(item7);
        Item item8 = new Item(R.string.fruits, R.drawable.fruit_100);
        items.add(item8);
        Item item9 = new Item(R.string.meat, R.drawable.meat_100);
        items.add(item9);
        Item item10 = new Item(R.string.home, R.drawable.ic_homeappliances_100);
        items.add(item10);
        Item item11 = new Item(R.string.toys, R.drawable.toys_100);
        items.add(item11);
        Item item12 = new Item(R.string.tools, R.drawable.construction_tool_100);
        items.add(item12);
        Item item13 = new Item(R.string.jewellery, R.drawable.jewel_100);
        items.add(item13);
        Item item14 = new Item(R.string.fashion, R.drawable.fashion_100);
        items.add(item14);
        Item item15 = new Item(R.string.grooming, R.drawable.grooming_100);
        items.add(item15);
        Item item16 = new Item(R.string.cookingOil, R.drawable.cookingoil_100);
        items.add(item16);
        Item item17 = new Item(R.string.Groceries, R.drawable.ic_homegroceries_100);
        items.add(item17);
        binding.query.setAdapter(new autoCompleteTextViewAdapter(this, items));
    }

    @Override
    public void onBackPressed()
    {
        if(activityChooseItemBinding.searchQuery.getVisibility() == View.VISIBLE)
        {
            activityChooseItemBinding.searchQuery.setVisibility(View.GONE);
            activityChooseItemBinding.searchBoxDifferentBackground.setVisibility(View.VISIBLE);
        }
        else if( activityChooseItemBinding.searchBoxDifferentBackground.getVisibility() == View.VISIBLE )
        {
            activityChooseItemBinding.searchBoxDifferentBackground.setVisibility(View.GONE);
            activityChooseItemBinding.searchText.setVisibility(View.VISIBLE);
            activityChooseItemBinding.findItemText.setVisibility(View.GONE);
            activityChooseItemBinding.items.setVisibility(View.VISIBLE);
            activityChooseItemBinding.searchBox.setVisibility(View.VISIBLE);
        }
        else
        {
            if(itemShopped)
                setResult(Activity.RESULT_OK);
            else
                setResult(Activity.RESULT_CANCELED);
            super.onBackPressed();
        }
    }
}