package com.example.sqlitecrudoperation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder>{

    private Context context;
    private ArrayList<PersonInfo> arrayList;
    private DatabaseHelper databaseHelper;

    public Adapter(Context context, ArrayList<PersonInfo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        PersonInfo info = arrayList.get(position);

        final String id = info.getId();
        final String name = info.getName();
        final String age = info.getAge();
        final String image = info.getImage();
        final String phone = info.getPhone();
        final String addTimeStamp = info.getAddTimeStamp();
        final String updateTimeStamp = info.getUpdateTimeStamp();

        holder.tvName.setText(name);
        holder.tvPhone.setText(phone);
        holder.tvAge.setText(age);
        holder.ivPerson.setImageURI(Uri.parse(image));

        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editDialog(
                        ""+position,
                        ""+id,
                        ""+name,
                        ""+age,
                        ""+phone,
                        ""+image,
                        ""+addTimeStamp,
                        ""+updateTimeStamp
                );
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog(""+id);
            }
        });

    }

    private void deleteDialog(String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are you want to delete data?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_action_delete);


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               databaseHelper.deleteInfo(id);
                ((MainActivity)context).onResume();
                Toast.makeText(context, "Item Deleted!", Toast.LENGTH_SHORT).show();
               dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void editDialog(String position, String id, String name, String age, String phone, String image, String addTimeStamp, String updateTimeStamp) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update");
        builder.setMessage("Are you want to update data?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_action_edit);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context,EditRecordActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                intent.putExtra("age",age);
                intent.putExtra("phone",phone);
                intent.putExtra("image",image);
                intent.putExtra("add_time",addTimeStamp);
                intent.putExtra("update_time",updateTimeStamp);
                intent.putExtra("editMode",true);
                context.startActivity(intent);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
           dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageView ivPerson, ivEdit, ivDelete;
        TextView tvName, tvAge, tvPhone;

        public Holder(@NonNull View itemView) {
            super(itemView);

            ivPerson = itemView.findViewById(R.id.ivPerson);
            tvName = itemView.findViewById(R.id.tvPersonName);
            tvAge = itemView.findViewById(R.id.tvPersonAge);
            tvPhone = itemView.findViewById(R.id.tvPersonPhone);
            ivEdit =itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);

        }
    }
}
