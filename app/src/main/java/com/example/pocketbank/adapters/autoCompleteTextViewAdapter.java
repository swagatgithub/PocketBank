package com.example.pocketbank.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.pocketbank.R;
import com.example.pocketbank.model.Item;
import com.example.pocketbank.others.chooseItem;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;
import java.util.List;

public class autoCompleteTextViewAdapter extends ArrayAdapter<Item>
{

    private chooseItem context;

    private ArrayList<Item> items;

    public autoCompleteTextViewAdapter(chooseItem context, ArrayList<Item> items)
    {
        super(context, 0, items);
        this.context = context;
        this.items = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.autocompletetextviewitemlayout, parent, false);
        Item currentItem = getItem(position);
        ((MaterialTextView) convertView.findViewById(R.id.itemNameForAutoCompleteTextView)).setText(getString(currentItem.getItemNameResourceId()));
        ((ShapeableImageView) convertView.findViewById(R.id.itemImageForAutoCompleteTextView)).setImageDrawable(getDrawable(currentItem.getItemImageResourceId()));
        return convertView;
    }

    private String getString(int resId)
    {
        return context.getString(resId);
    }

    private Drawable getDrawable(int resId)
    {
        return context.getDrawable(resId);
    }

    @NonNull
    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                System.out.println("perform Filtering has been called..");
                FilterResults filterResults = new FilterResults();
                List<Item> itemList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0)
                {
                    itemList.addAll(items);
                    context.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            context.findItemText.setVisibility(View.VISIBLE);
                            context.findItemText.setText(R.string.findItemYouHaveShopped);
                        }

                    });

                }
                else
                {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for(Item item : items)
                    {
                        if (getString(item.getItemNameResourceId()).toLowerCase().contains(filterPattern))
                        {
                            System.out.println("if filterPattern is"+filterPattern);
                            context.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    context.findItemText.setVisibility(View.GONE);
                                }
                            });
                            itemList.add(item);
                        }
                    }
                }
                if(itemList.size() == 0)
                {

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            context.findItemText.setVisibility(View.VISIBLE);
                            context.findItemText.setText("Couldn't find "+'"'+constraint+'"');
                        }

                    });

                }

                filterResults.values = itemList;
                filterResults.count = itemList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                clear();
                addAll((List) results.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue)
            {
                //ArrayList<String> arrayList = new ArrayList<>();

                //Implement Click Logic Over Here..
                return  getString( ((Item)resultValue).getItemNameResourceId());
            }

        };
    }

}