package com.example.pocketbank.adapters;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.CombinedVibration;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pocketbank.R;
import com.example.pocketbank.databinding.ItemRecElementLayoutBinding;
import com.example.pocketbank.myApplication;
import com.example.pocketbank.others.chooseItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.myViewHolder>
{
    private final ArrayList<Integer> itemNames;
    private final ArrayList<Integer> resIds;
    private final Object object;
    private final MaterialButton doneButton;
    private int imageResourceId , nameResourceId;
    private ViewParent previousViewParent;
    private final chooseItem context;

    public itemAdapter(ArrayList<Integer> resIds , ArrayList<Integer> itemNames, Object object, ViewGroup viewGroup, chooseItem context)
    {
        this.itemNames = itemNames;
        this.resIds = resIds;
        this.object = object;
        this.context = context;

        doneButton = viewGroup.findViewById(R.id.doneButton);

        doneButton.setOnClickListener( v ->
                myApplication.getExecutorService().execute(() -> {
                    SQLiteDatabase database = myApplication.writeableDatabase;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("itemImageResourceId" , imageResourceId);
                    contentValues.put("itemNameResourceId",nameResourceId);
                    long insertValue = database.insert("items",null,contentValues);
                    if(insertValue != -1)
                    {
                        System.out.println("inside if block..");
                        context.runOnUiThread(() -> {
                            context.itemShopped = true;
                            context.onBackPressed();
                        });
                    }


                }));

    }

     class myViewHolder extends RecyclerView.ViewHolder
    {
        public ShapeableImageView itemImage;
        public MaterialTextView itemName;
        
        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            ShapeableImageView checkedImage = itemView.findViewById(R.id.checkedImage);
            itemName = itemView.findViewById(R.id.itemName);
            
            itemImage.setOnClickListener(v ->
            {
                if(checkedImage.getVisibility() == View.GONE) 
                {
                    imageResourceId =(Integer)itemImage.getTag();
                    nameResourceId = (Integer)itemName.getTag();

                    checkedImage.setVisibility(View.VISIBLE);
                    doneButton.setVisibility(View.VISIBLE);
                    if (previousViewParent != null && previousViewParent != checkedImage.getParent())
                        ((ViewGroup) previousViewParent).findViewById(R.id.checkedImage).setVisibility(View.GONE);
                    previousViewParent = checkedImage.getParent();
                }
                else
                {
                    checkedImage.setVisibility(View.GONE);
                    doneButton.setVisibility(View.GONE);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                    ((Vibrator) object).vibrate(20);
                else
                    ((VibratorManager) object).vibrate(CombinedVibration.createParallel(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)));
            });

        }

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ItemRecElementLayoutBinding binding = ItemRecElementLayoutBinding.inflate(LayoutInflater.from( parent.getContext() ) ,parent, false);
        return new myViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position)
    {
        holder.itemImage.setImageResource(resIds.get(position));
        holder.itemImage.setTag(resIds.get(position));
        holder.itemName.setText(itemNames.get(position));
        holder.itemName.setTag(itemNames.get(position));
    }

    @Override
    public int getItemCount()
    {
        return resIds.size();
    }

}
