package hk.edu.cuhk.ie.iems5722.a2_1155162628;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatroomAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Chatroom> list;
    public ChatroomAdapter(Context context, List<Chatroom>list){
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
        TextView tvName;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.chatroom_item,null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.chatroom_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvName.setText((String)list.get(position).getName());
        return convertView;
    }
}
