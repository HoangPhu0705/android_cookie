package tdtu.edu.cookie.Database.MainDatabase;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import tdtu.edu.cookie.Database.Dao.FolderDao;
import tdtu.edu.cookie.Database.Dao.TopicDao;
import tdtu.edu.cookie.Database.Dao.UserDao;
import tdtu.edu.cookie.Database.Dao.WordsDao;
import tdtu.edu.cookie.Database.Entity.BitmapConverter;
import tdtu.edu.cookie.Database.Entity.Folder;
import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.User;
import tdtu.edu.cookie.Database.Entity.Words;

@Database(entities = {Words.class, Topic.class, Folder.class, User.class}, version = 6)
@TypeConverters(BitmapConverter.class) // Add this line to use the BitmapConverter
public abstract class CookieDatabase extends RoomDatabase {
    public abstract FolderDao folderDao();
    public abstract TopicDao topicDao();
    public abstract WordsDao wordsDao();
    public abstract UserDao UserDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE Words_new (id INTEGER PRIMARY KEY NOT NULL, english_word TEXT, vietnamese_word TEXT, spelling TEXT, date TEXT, image BLOB, word_form TEXT, is_fav INTEGER NOT NULL, topic_id INTEGER NOT NULL)");

            // Chép dữ liệu từ bảng cũ sang bảng tạm thời
            database.execSQL("INSERT INTO Words_new (id, english_word, vietnamese_word, spelling, date, image, word_form, is_fav, topic_id) SELECT id, english_word, vietnamese_word, spelling, date, image, word_form, is_fav, topic_id FROM Words");

            // Xóa bảng cũ
            database.execSQL("DROP TABLE Words");

            // Đổi tên của bảng tạm thời thành tên của bảng cũ
            database.execSQL("ALTER TABLE Words_new RENAME TO Words");
        }
    };

    private static CookieDatabase INSTANCE;

    public static CookieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CookieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CookieDatabase.class, "cookie.db")
//                            .addMigrations(MIGRATION_1_2)
//                            .createFromAsset("cookie.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
