package hk.edu.cuhk.ie.iems5722.a2_1155162628;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MsgAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private List<Msg> list;
    private String user_id = "1111111111";
    private String user_name = "Niko";

    public MsgAdapter(Context context, List<Msg>list){
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        TextView tvNameRight;
        TextView tvMessageRight;
        TextView tvTimeRight;
        TextView tvNameLeft;
        TextView tvMessageLeft;
        TextView tvTimeLeft;
        LinearLayout layoutRight;
        LinearLayout layoutLeft;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.msg_item,null);

            viewHolder.layoutRight = (LinearLayout) convertView.findViewById(R.id.layout_right);
            viewHolder.layoutLeft = (LinearLayout)convertView.findViewById(R.id.layout_left);

            viewHolder.tvNameRight = (TextView) convertView.findViewById(R.id.tv_name_right);
            viewHolder.tvMessageRight = (TextView) convertView.findViewById(R.id.tv_message_right);
            viewHolder.tvTimeRight = (TextView) convertView.findViewById(R.id.tv_time_right);

            viewHolder.tvNameLeft = (TextView) convertView.findViewById(R.id.tv_name_left);
            viewHolder.tvMessageLeft = (TextView) convertView.findViewById(R.id.tv_message_left);
            viewHolder.tvTimeLeft = (TextView) convertView.findViewById(R.id.tv_time_left);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (list.get(position).getName().equals(user_name)){
            viewHolder.layoutLeft.setVisibility(View.GONE);
            viewHolder.layoutRight.setVisibility(View.VISIBLE);

            viewHolder.tvNameRight.setText("User: "+list.get(position).getName());
            viewHolder.tvMessageRight.setText(list.get(position).getMessage());
            viewHolder.tvTimeRight.setText(list.get(position).getTime());
        }
        else {
            viewHolder.layoutLeft.setVisibility(View.VISIBLE);
            viewHolder.layoutRight.setVisibility(View.GONE);

            viewHolder.tvNameLeft.setText("User: "+list.get(position).getName());
            viewHolder.tvMessageLeft.setText(list.get(position).getMessage());
            viewHolder.tvTimeLeft.setText(list.get(position).getTime());
        }
        return convertView;
    }
}
