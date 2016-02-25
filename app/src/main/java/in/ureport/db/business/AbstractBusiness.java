package in.ureport.db.business;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

import in.ureport.db.repository.AbstractRepository;

/**
 * Created by johndalton on 21/10/14.
 */
public class AbstractBusiness<Type extends Model> implements AbstractRepository<Type> {

    private Class<Type> typeClass;

    public AbstractBusiness(Class<Type> typeClass) {
        this.typeClass = typeClass;
    }

    public long create(Type object) {
        return object.save();
    }

    public void create(List<Type> objects) {
        ActiveAndroid.beginTransaction();

        try {
            for(Type object : objects) {
                object.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void delete(Type object) {
        object.delete();
    }

    public void delete(List<Type> objects) {
        ActiveAndroid.beginTransaction();

        try {
            for(Type object : objects) {
                object.delete();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void deleteAll() {
        ActiveAndroid.beginTransaction();

        try {
            List<Type> objects = getAll();
            for(Type object : objects) {
                object.delete();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public long update(Type object) {
        return object.save();
    }

    public Type get(Long id) {
        return new Select().from(typeClass)
                .where("id = ?", id)
                .executeSingle();
    }

    public List<Type> getAll() {
        return new Select().from(typeClass)
                .execute();
    }

    protected Class<Type> getTypeClass() {
        return typeClass;
    }
}
