package in.ureport.db.repository;

import java.util.List;

/**
 * Created by johndalton on 21/10/14.
 */
public interface AbstractRepository<Type> {

    public long create(Type object);

    public void create(List<Type> objects);

    public void delete(List<Type> objects);

    public void deleteAll();

    public void delete(Type object);

    public long update(Type object);

    public Type get(Long id);

    public List<Type> getAll();

}
