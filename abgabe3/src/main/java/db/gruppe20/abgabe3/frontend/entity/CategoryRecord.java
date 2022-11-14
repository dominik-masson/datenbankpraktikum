package db.gruppe20.abgabe3.frontend.entity;

import java.util.List;

public record CategoryRecord(Integer id, String name, String parentCategoryName, List<String> subCategoryNames) {


}