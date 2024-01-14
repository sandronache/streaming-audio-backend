package application.entities.pages.typevisitor;

import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.pages.HomePage;
import application.entities.pages.LikedContentPage;

/**
 * Visitor that returns the type of the page
 */
public final class TypeVisitor implements TypePageVisitor {
    /**
     * Returns the type of the page
     * @param page
     * @return
     */
    @Override
    public String visit(final LikedContentPage page) {
        return "likedContentPage";
    }

    /**
     * Returns the type of the page
     * @param page
     * @return
     */
    @Override
    public String visit(final Artist page) {
        return "artist";
    }

    /**
     * Returns the type of the page
     * @param page
     * @return
     */
    @Override
    public String visit(final Host page) {
        return "host";
    }

    /**
     * Returns the type of the page
     * @param page
     * @return
     */
    @Override
    public String visit(final HomePage page) {
        return "homePage";
    }
}
