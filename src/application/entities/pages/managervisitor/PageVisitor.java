package application.entities.pages.managervisitor;

import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.pages.HomePage;
import application.entities.pages.LikedContentPage;

/**
 * Interface for the page visitor
 */
public interface PageVisitor {
    /**
     * visit for like content page
     * @param page
     */
    void visit(LikedContentPage page);

    /**
     * visit for artist
     * @param page
     */
    void visit(Artist page);

    /**
     * visit for host
     * @param page
     */
    void visit(Host page);

    /**
     * visit for home page
     * @param page
     */
    void visit(HomePage page);
}
