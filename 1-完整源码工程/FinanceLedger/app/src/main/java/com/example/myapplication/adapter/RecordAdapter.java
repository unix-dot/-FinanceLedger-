package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.FinanceRecord;

import java.util.List;
import java.util.Locale;

/**
 * 财务账单流水列表适配器 (ListView使用)
 */
public class RecordAdapter extends BaseAdapter {

    private Context context;
    private List<FinanceRecord> recordList;
    private LayoutInflater inflater;

    public RecordAdapter(Context context, List<FinanceRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return recordList != null ? recordList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_record, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = convertView.findViewById(R.id.iv_record_icon);
            holder.tvTitle = convertView.findViewById(R.id.tv_record_title);
            holder.tvSub = convertView.findViewById(R.id.tv_record_sub);
            holder.tvTags = convertView.findViewById(R.id.tv_record_tags);
            holder.tvAmount = convertView.findViewById(R.id.tv_record_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FinanceRecord item = recordList.get(position);

        String remarkStr = (item.getRemark() != null && !item.getRemark().trim().isEmpty())
                ? item.getRemark() : "未命名交易";
        holder.tvTitle.setText(item.getType() + " - " + remarkStr);
        holder.tvSub.setText(item.getAccount() + " · " + item.getTime());

        // 标签拼接
        StringBuilder tags = new StringBuilder();
        if (item.isBudget()) {
            tags.append("[硬性预算] ");
        }
        if (item.isUrgent()) {
            tags.append("[大额关注] ");
        }
        if (item.isRemind()) {
            tags.append("[到期提醒]");
        }
        if (tags.length() == 0) {
            tags.append("[常规流水]");
        }
        holder.tvTags.setText(tags.toString());

        // 金额区分收支颜色
        if ("支出".equals(item.getType())) {
            holder.tvAmount.setText(String.format(Locale.CHINA, "-¥ %.2f", item.getAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#D32F2F"));
            holder.ivIcon.setImageResource(R.drawable.ic_finance);
        } else {
            holder.tvAmount.setText(String.format(Locale.CHINA, "+¥ %.2f", item.getAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#2E7D32"));
            holder.ivIcon.setImageResource(R.drawable.ic_chart);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvSub;
        TextView tvTags;
        TextView tvAmount;
    }
}
