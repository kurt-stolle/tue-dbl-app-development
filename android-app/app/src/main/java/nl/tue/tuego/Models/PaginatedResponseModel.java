package nl.tue.tuego.Models;

public class PaginatedResponseModel<T> {
    public T Data[];
    public Object Pagination; // We use object because pagination is currently not implemented
}
