
package com.example.qlysv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private ArrayList<TodoModel> list;

    public TodoAdapter(ArrayList<TodoModel> list) {
        this.list = list;
    }

    // Click interface
    public interface OnItemClickListener {
        void onEdit(TodoModel model);
        void onDelete(TodoModel model);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMaSV, tvTitle, tvDate, tvType;
        TextView tvContent, tvLop, tvSoDienThoai;

        Button btnSua, btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMaSV = itemView.findViewById(R.id.tvItemMaSV);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDate = itemView.findViewById(R.id.tvItemDate);
            tvType = itemView.findViewById(R.id.tvItemType);
            tvContent = itemView.findViewById(R.id.tvItemContent);
            tvLop = itemView.findViewById(R.id.tvItemLop);
            tvSoDienThoai = itemView.findViewById(R.id.tvItemSoDienThoai);

            btnSua = itemView.findViewById(R.id.btnItemSua);
            btnXoa = itemView.findViewById(R.id.btnItemXoa);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        TodoModel model = list.get(position);

        holder.tvMaSV.setText("Mã sinh viên: " + model.getMaSV());
        holder.tvTitle.setText("Họ tên: " + model.getTitle());
        holder.tvDate.setText("Ngày sinh: " + model.getDate());
        holder.tvType.setText("Giới tính: " + model.getType());
        holder.tvContent.setText("Địa chỉ: " + model.getContent());
        holder.tvLop.setText("Mã lớp: " + model.getLop());
        holder.tvSoDienThoai.setText(
                "Số điện thoại: " + model.getSoDienThoai()
        );

        // Button Sửa
        holder.btnSua.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(model);
            }
        });

        // Button Xóa
        holder.btnXoa.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
