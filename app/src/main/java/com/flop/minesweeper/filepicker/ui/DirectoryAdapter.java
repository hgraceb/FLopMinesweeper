package com.flop.minesweeper.filepicker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flop.minesweeper.R;
import com.flop.minesweeper.errorLogInfo.FlopApplication;
import com.flop.minesweeper.filepicker.utils.FileSizeUtil;
import com.flop.minesweeper.filepicker.utils.FileTypeUtils;
import com.flop.minesweeper.filepicker.utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Dimorinny on 24.10.15.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class DirectoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView mFileImage;
        private TextView mFileTitle;
        private TextView mFileSubtitle;

        public DirectoryViewHolder(View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getAdapterPosition());
                }
            });

            mFileImage = (ImageView) itemView.findViewById(R.id.item_file_image);
            mFileTitle = (TextView) itemView.findViewById(R.id.item_file_title);
            mFileSubtitle = (TextView) itemView.findViewById(R.id.item_file_subtitle);
        }
    }

    private List<File> mFiles;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public DirectoryAdapter(Context context, List<File> files) {
        mContext = context;
        mFiles = files;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);

        return new DirectoryViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, int position) {
        File currentFile = mFiles.get(position);

        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);
        holder.mFileImage.setImageResource(fileType.getIcon());
        String string = FlopApplication.getInstance().getString(R.string.type_directory);
        // FLOP修改：修改文件描述
        // 如果是文件夹
        if (currentFile.isDirectory()) {
            // 只显示是文件夹
            holder.mFileSubtitle.setText(fileType.getDescription());
        } else {
            // 如果是文件
            String dateTime = FileUtils.getDateTime(currentFile.getAbsolutePath());
            String filesSize = FileSizeUtil.getAutoFileOrFilesSize(currentFile);
            // 显示文件大小和最后修改时间
            holder.mFileSubtitle.setText(String.format(mContext.getString(R.string.file_desc), filesSize, dateTime));
        }
        holder.mFileTitle.setText(currentFile.getName());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public File getModel(int index) {
        return mFiles.get(index);
    }
}