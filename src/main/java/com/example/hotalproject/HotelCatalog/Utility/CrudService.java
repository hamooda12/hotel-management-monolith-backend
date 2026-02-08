package com.example.hotalproject.HotelCatalog.Utility;


    public interface CrudService<T, ID> {
        T create(T body);
        T getById(ID id);
        T update(ID id, T body);
        void delete(ID id);
    }


