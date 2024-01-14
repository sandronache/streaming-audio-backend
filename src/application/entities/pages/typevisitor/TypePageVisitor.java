package application.entities.pages.typevisitor;

import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.pages.HomePage;
import application.entities.pages.LikedContentPage;

/**
 * Interface for type page visitor
 */
public interface TypePageVisitor {
        /**
         * visit for like content page
         * @param page
         */
        String visit(LikedContentPage page);

        /**
         * visit for artist
         * @param page
         */
        String visit(Artist page);

        /**
         * visit for host
         * @param page
         */
        String visit(Host page);

        /**
         * visit for home page
         * @param page
         */
        String visit(HomePage page);
}
