package application.entities.pages;

import application.entities.pages.visitor.PageVisitor;

/**
 * Interface for all types of pages
 */
public interface Page {
    /**
     * All classes that implement this interface
     * accept to be visited
     * @param visitor
     */
    void accept(PageVisitor visitor);

    /**
     * Method that says with page is this interface
     * @return: 0 - home page
     *          1 - liked page
     *          2 - artist page
     *          3 - host page
     */
    int whichPage();
}
