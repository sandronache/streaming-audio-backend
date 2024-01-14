package application.entities.pages;

import application.entities.pages.managervisitor.PageVisitor;
import application.entities.pages.typevisitor.TypePageVisitor;

/**
 * Interface for all types of pages
 */
public interface Page {
    /**
     * All classes that implement this interface
     * accept to be visited by PageVisitor
     * @param visitor
     */
    void accept(PageVisitor visitor);

    /**
     * All classes that implement this interface
     * accept to be visited by TypePageVisitor
     * @param visitor
     */
    String accept(TypePageVisitor visitor);
}
