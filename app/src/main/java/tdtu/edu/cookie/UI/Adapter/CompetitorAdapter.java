package tdtu.edu.cookie.UI.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import tdtu.edu.cookie.Database.Entity.Competitors;
import tdtu.edu.cookie.R;

public class CompetitorAdapter extends RecyclerView.Adapter<CompetitorAdapter.viewHolder> {

    ArrayList<Competitors> competitors;
    Context context;
    public CompetitorAdapter(Context context, ArrayList<Competitors> competitors ){
        this.context = context;
        this.competitors = competitors;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.bxh_item, parent, false);
        return new CompetitorAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.name.setText(competitors.get(position).getName());
        holder.email.setText(competitors.get(position).getEmail());
        holder.score.setText(competitors.get(position).getScore());
        holder.time.setText(formatTimestamp(competitors.get(position).getTimestamp().toDate()) );
        holder.stt.setText(competitors.get(position).stt);
        getUserProfileImage(holder.imageView,competitors.get(position).id);
    }
    private String formatTimestamp(Date date) {
        // Định dạng theo chuỗi bạn mong muốn
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' HH:mm:ss");

        return sdf.format(date);
    }
    public void getUserProfileImage(final ImageView imageView,String id) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + id + "/profile.jpg");
        if(storageReference!=null){
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // The download URL of the image
                    String imageUrl = uri.toString();

                    // Load the image into the CircleImageView
                    Picasso.get().load(imageUrl).into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    imageView.setImageResource(R.drawable.student_avatar);

                    Log.d("TAG", exception.getMessage());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return competitors.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {
        TextView score;
        TextView name;
        TextView email;
        TextView time;
        TextView stt;
        ImageView imageView;

        public viewHolder(@NonNull View itemView) {



            super(itemView);

            score = itemView.findViewById(R.id.score);
            name = itemView.findViewById(R.id.student_name);
            email = itemView.findViewById(R.id.student_email);
            time = itemView.findViewById(R.id.time);
            stt=itemView.findViewById(R.id.student_id);
            imageView = itemView.findViewById(R.id.ava);
        }
    }
}
