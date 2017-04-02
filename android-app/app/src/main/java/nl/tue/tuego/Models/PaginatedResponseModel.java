package nl.tue.tuego.Models;

import java.util.List;

public class PaginatedResponseModel<T> {
    public T Data[];
    public Object Pagination; // We use object because pagination is currently not implemented
}
