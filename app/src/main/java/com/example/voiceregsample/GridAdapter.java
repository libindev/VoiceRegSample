package com.example.voiceregsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.EditText;

public class GridAdapter extends BaseAdapter {
   private Context mContext;
  static int currentPosition =0;
   

   public GridAdapter(Context c) {
      mContext = c;
   }
   
   public int getCount() {
      return mThumbIds.length;
   }

   public Object getItem(int position) {
      return null;
   }

   public long getItemId(int position) {
      return 0;
   }
   

   public View getView(int position, View convertView, ViewGroup parent) {

       LayoutInflater inflater = (LayoutInflater) mContext
               .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       View gridView = null;
      if (convertView == null) {
          gridView = new View(mContext);
          gridView = inflater.inflate( R.layout.grid_item , null);
          EditText ed= gridView.findViewById(R.id.editText);
          ed.setOnTouchListener((v, event) -> {
              currentPosition=position;
              return false;
          });
      }
      else 
      { gridView = convertView;
      }

      return gridView;
   }

   
   public String[] mThumbIds = {
      "", "","", "","", "","", "","", "","", "","", "","", "","", "","","","",""

   };
}