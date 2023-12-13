package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class ItemRepository {
    private static final Map<Long, Item> store = new HashMap<>();
    private static long sequene = 0L;

    public Item save(Item item){
        item.setId(++sequene);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id){
        return store.get(id);
    }

    public List<Item> findAll(){
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, Item updataParam){
        Item findItem = findById(itemId);
        findItem.setItemName(updataParam.getItemName());
        findItem.setPrice(updataParam.getPrice());
        findItem.setQuantity(updataParam.getQuantity());
    }

    public void delete(Long itemId){
        store.remove(itemId);
    }

    public void clearStore(){
        store.clear();
    }
}
