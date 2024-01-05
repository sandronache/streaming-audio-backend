package application;

import application.commands.admin.AddUser;
import application.commands.admin.DeleteUser;
import application.commands.admin.ShowAlbums;
import application.commands.admin.ShowPodcasts;
import application.commands.admin.Wrapped;
import application.commands.admin.merchandise.BuyMerch;
import application.commands.admin.merchandise.SeeMerch;
import application.commands.admin.notifications.GetNotifications;
import application.commands.admin.notifications.Subscribe;
import application.commands.admin.premium.BuyPremium;
import application.commands.admin.premium.CancelPremium;
import application.commands.player.AddRemoveInPlaylist;
import application.commands.player.Backward;
import application.commands.player.Forward;
import application.commands.player.Like;
import application.commands.player.Load;
import application.commands.player.Next;
import application.commands.player.PlayPause;
import application.commands.player.Prev;
import application.commands.player.Repeat;
import application.commands.player.Shuffle;
import application.commands.player.Status;
import application.commands.playlists.CreatePlaylist;
import application.commands.playlists.FollowPlaylist;
import application.commands.playlists.ShowPlaylists;
import application.commands.playlists.SwitchVisibility;
import application.commands.searchbar.Search;
import application.commands.searchbar.Select;
import application.commands.statistics.GetAllUsers;
import application.commands.statistics.GetOnlineUsers;
import application.commands.statistics.GetTop5Albums;
import application.commands.statistics.GetTop5Artists;
import application.commands.statistics.GetTop5Playlists;
import application.commands.statistics.GetTop5Songs;
import application.commands.statistics.ShowPreferredSongs;
import application.commands.users.artists.AddAlbum;
import application.commands.users.artists.AddEvent;
import application.commands.users.artists.AddMerch;
import application.commands.users.artists.RemoveAlbum;
import application.commands.users.artists.RemoveEvent;
import application.commands.users.hosts.AddAnnouncement;
import application.commands.users.hosts.AddPodcast;
import application.commands.users.hosts.RemoveAnnouncement;
import application.commands.users.hosts.RemovePodcast;
import application.commands.users.normal.SwitchConnectionStatus;
import application.commands.users.normal.pages.ChangePage;
import application.commands.users.normal.pages.PrintCurrentPage;
import application.entities.input.Command;
import application.entities.library.Episode;
import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.library.Song;
import application.entities.library.users.AccountArtist;
import application.entities.library.users.normal.User;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Run class starts the program;
 * <p>
 *     Contains the data for the application: all players, general information,
 * commands to run and outputs.
 * </p>
 */
@Getter
public final class Run {
    private Library library;
    private ArrayNode outputs;
    private List<Command> commands;
    private ObjectMapper objectMapper;
    private Integer globalPlaylistId;
    private ArrayList<Player> players;
    private Integer lastTimestamp;

    // Singleton
    @Getter
    private static final Run INSTANCE_RUN = new Run();

    private Run() { }

    /**
     * Transforms the input library in a custom library
     * @param libraryInput - the input library
     * @return - my custom library
     */
    private Library buildLibrary(final LibraryInput libraryInput) {
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Podcast> podcasts = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        ArrayList<AccountArtist> accounts =  new ArrayList<>();
        // move each song
        for (SongInput inputSong: libraryInput.getSongs()) {
            Song newInputSong = new Song(inputSong.getName(),
                    inputSong.getDuration(), inputSong.getAlbum(),
                    inputSong.getTags(), inputSong.getLyrics(),
                    inputSong.getGenre(), inputSong.getReleaseYear(),
                    inputSong.getArtist());
            songs.add(newInputSong);
            // we create an account for each new artist on the platform
            boolean tOrF = false;
            for (AccountArtist iterAccount: accounts) {
                if (iterAccount.getUsername().equals(newInputSong.getArtist())) {
                    tOrF = true;
                    break;
                }
            }
            if (!tOrF) {
                accounts.add(new AccountArtist(newInputSong.getArtist()));
            }
        }
        // move each user
        for (UserInput inputUser: libraryInput.getUsers()) {
            User newInputUser = new User(inputUser.getUsername(),
                    inputUser.getAge(), inputUser.getCity());
            users.add(newInputUser);
        }
        // move each podcast
        for (PodcastInput inputPodcast: libraryInput.getPodcasts()) {
            ArrayList<Episode> episodes = new ArrayList<>();
            for (EpisodeInput inputEpisode : inputPodcast.getEpisodes()) {
                Episode newInputEpisode = new Episode(inputEpisode.getName(),
                        inputEpisode.getDuration(), inputEpisode.getDescription());
                episodes.add(newInputEpisode);
            }
            Podcast newInputPodcast = new Podcast(inputPodcast.getName(),
                    inputPodcast.getOwner(), episodes);
            podcasts.add(newInputPodcast);
        }
        return new Library(songs, podcasts, users, accounts);
    }

    /**
     * Method that initiates the application with:
     * @param libraryInput - the library with songs, podcasts and users;
     * @param outputsInput - output array;
     * @param commandsInput - the commands given as input;
     * @param objectMapperInput - the object Mapper;
     */

    public void buildRun(final LibraryInput libraryInput, final ArrayNode outputsInput,
                final List<Command> commandsInput, final ObjectMapper objectMapperInput) {
        INSTANCE_RUN.library = this.buildLibrary(libraryInput);
        INSTANCE_RUN.outputs = outputsInput;
        INSTANCE_RUN.commands = commandsInput;
        INSTANCE_RUN.objectMapper = objectMapperInput;
        INSTANCE_RUN.globalPlaylistId = null;
        INSTANCE_RUN.players = new ArrayList<>();
    }

    /**
     * Updates the player
     * @param currentPlayer
     * @param timestamp
     */
    public void updatePlayer(final Player currentPlayer,
                             final Integer timestamp) {
        switch (currentPlayer.getType()) {
            case "song" -> currentPlayer.checkIfEndedSong(timestamp);
            case "podcast" -> currentPlayer.checkIfEndedPodcast(timestamp);
            case "album" -> currentPlayer.checkIfEndedAlbum(timestamp);
            case "playlist" -> currentPlayer.checkIfEndedPlaylist(timestamp);
            default -> { }
        }
    }

    /**
     * Updates all players to this last timestamp
     */
    private void updateAllPlayers() {
        for (Player player: players) {
            User user = library.getUser(player.getUsername());
            if (!player.getName().isEmpty() && !player.isPaused()
                    && user.isStatus()) {
                this.updatePlayer(player, lastTimestamp);
            }
            user.cancelPremiumManagement(library);
        }
    }

    /**
     * This method prints the status of all artists accounts
     */
    public void endProgram() {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", "endProgram");
        // new node for results;
        ObjectNode node1 = objectMapper.createObjectNode();
        // we sort the accounts;
        library.sortAccounts();
        // we start printing each account
        for (int i = 0; i < library.getAccountsAllArtists().size(); i++) {
            // temp account
            AccountArtist tempAccount = library.getAccountsAllArtists().get(i);
            // we check if the artist was played at all
            if (!tempAccount.isPlayedOrNot()) {
                continue;
            }
            // temp node
            ObjectNode iterNode = objectMapper.createObjectNode();
            // we round the numbers
            tempAccount.roundDoubles();
            iterNode.put("songRevenue", tempAccount.getSongRevenue());
            iterNode.put("merchRevenue", tempAccount.getMerchRevenue());
            iterNode.put("ranking", i + 1);
            // find the most profitable song
            Song mostProfSong = library.getArtist(tempAccount.getUsername())
                    .findMostProfitableSong();
            if (mostProfSong == null) {
                iterNode.put("mostProfitableSong", "N/A");
            } else {
                iterNode.put("mostProfitableSong", mostProfSong.getName());
            }
            // set the artist
            node1.set(tempAccount.getUsername(), iterNode);
        }
        // we set it
        node.set("result", node1);
        // add to outputs
        outputs.add(node);
    }

    /**
     * Starting point of the application;
     * <p>
     *     Executes one command at a time.
     * </p>
     */
    public void startPoint() {
        for (Command command : commands) {
            switch (command.getCommand()) {
                case "search" -> {
                    Search searchCommand = new Search(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getType(), command.getFilters(), library, players);
                    searchCommand.startCommand(objectMapper, outputs);
                }
                case "select" -> {
                    Select selectCommand = new Select(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getItemNumber(), library);
                    selectCommand.startCommand(objectMapper, outputs);
                }
                case "load" -> {
                    Load loadCommand = new Load(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    loadCommand.startCommand(objectMapper, outputs);
                }
                case "playPause" -> {
                    PlayPause playPauseCommand = new PlayPause(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library,
                            players);
                    playPauseCommand.startCommand(objectMapper, outputs);
                }
                case "repeat" -> {
                    Repeat repeat = new Repeat(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    repeat.startCommand(objectMapper, outputs);
                }
                case "shuffle" -> {
                    Shuffle shuffle = new Shuffle(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getSeed(), library, players);
                    shuffle.startCommand(objectMapper, outputs);
                }
                case "forward" -> {
                    Forward forward = new Forward(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    forward.startCommand(objectMapper, outputs);
                }
                case "backward" -> {
                    Backward backward = new Backward(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    backward.startCommand(objectMapper, outputs);
                }
                case "like" -> {
                    Like like = new Like(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    like.startCommand(objectMapper, outputs);
                }
                case "next" -> {
                    Next next = new Next(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    next.startCommand(objectMapper, outputs);
                }
                case "prev" -> {
                    Prev prev = new Prev(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    prev.startCommand(objectMapper, outputs);
                }
                case "addRemoveInPlaylist" -> {
                    AddRemoveInPlaylist addRemoveInPlaylist = new AddRemoveInPlaylist(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), command.getPlaylistId(),
                            library, players);
                    addRemoveInPlaylist.startCommand(objectMapper, outputs);
                }
                case "status" -> {
                    Status statusCommand = new Status(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    statusCommand.startCommand(objectMapper, outputs);
                }
                case "createPlaylist" -> {
                    CreatePlaylist createPlaylist = new CreatePlaylist(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getPlaylistName(), globalPlaylistId, library);
                    createPlaylist.startCommand(objectMapper, outputs);
                    if (library.typeOfUser(createPlaylist.getUsername()) == 1
                            && library.getUser(createPlaylist.getUsername()).isStatus()) {
                        globalPlaylistId = createPlaylist.getGlobalPlaylistId();
                    }
                }
                case "switchVisibility" -> {
                    SwitchVisibility switchVisibility = new SwitchVisibility(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), command.getPlaylistId(), library);
                    switchVisibility.startCommand(objectMapper, outputs);
                }
                case "follow" -> {
                    FollowPlaylist follow = new FollowPlaylist(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                    follow.startCommand(objectMapper, outputs);
                }
                case "showPlaylists" -> {
                    ShowPlaylists showPlaylists = new ShowPlaylists(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library);
                    showPlaylists.startCommand(objectMapper, outputs);
                }
                case "showPreferredSongs" -> {
                    ShowPreferredSongs showPreferredSongs = new ShowPreferredSongs(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library);
                    showPreferredSongs.startCommand(objectMapper, outputs);
                }
                case "getTop5Songs" -> {
                    GetTop5Songs getTop5Songs = new GetTop5Songs(command.getCommand(),
                            command.getTimestamp(), library);
                    getTop5Songs.startCommand(objectMapper, outputs);
                }
                case "getTop5Playlists" -> {
                    GetTop5Playlists getTop5Playlists = new GetTop5Playlists(command.getCommand(),
                            command.getTimestamp(), library);
                    getTop5Playlists.startCommand(objectMapper, outputs);
                }
                case "switchConnectionStatus" -> {
                    SwitchConnectionStatus switchConnectionStatus = new SwitchConnectionStatus(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, players);
                    switchConnectionStatus.startCommand(objectMapper, outputs);
                }
                case "getOnlineUsers" -> {
                    GetOnlineUsers getOnlineUsers = new GetOnlineUsers(command.getCommand(),
                            command.getTimestamp(), library);
                    getOnlineUsers.startCommand(objectMapper, outputs);
                }
                case "addUser" -> {
                    AddUser addUser = new AddUser(command.getCommand(), command.getTimestamp(),
                            command.getType(), library, command.getUsername(), command.getAge(),
                            command.getCity());
                    addUser.startCommand(objectMapper, outputs);
                }
                case "addAlbum" -> {
                    AddAlbum addAlbum = new AddAlbum(command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, command.getName(),
                            command.getReleaseYear(), command.getDescription(),
                            command.getSongs());
                    addAlbum.startCommand(objectMapper, outputs);
                }
                case "addEvent" -> {
                    AddEvent addEvent = new AddEvent(command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, command.getName(),
                            command.getDescription(), command.getDate());
                    addEvent.startCommand(objectMapper, outputs);
                }
                case "removeEvent" -> {
                    RemoveEvent removeEvent = new RemoveEvent(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library);
                    removeEvent.startCommand(objectMapper, outputs);
                }
                case "addMerch" -> {
                    AddMerch addMerch = new AddMerch(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, command.getName(), command.getDescription(),
                            command.getPrice());
                    addMerch.startCommand(objectMapper, outputs);
                }
                case "addPodcast" -> {
                    AddPodcast addPodcast = new AddPodcast(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library,
                            command.getName(), command.getEpisodes(),
                            players);
                    addPodcast.startCommand(objectMapper, outputs);
                }
                case "addAnnouncement" -> {
                    AddAnnouncement addAnnouncement = new AddAnnouncement(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, command.getName(), command.getDescription());
                    addAnnouncement.startCommand(objectMapper, outputs);
                }
                case "removeAnnouncement" -> {
                    RemoveAnnouncement removeAnnouncement = new RemoveAnnouncement(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), command.getName(), library);
                    removeAnnouncement.startCommand(objectMapper, outputs);
                }
                case "showAlbums" -> {
                    ShowAlbums showAlbums = new ShowAlbums(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library);
                    showAlbums.startCommand(objectMapper, outputs);
                }
                case "showPodcasts" -> {
                    ShowPodcasts showPodcasts = new ShowPodcasts(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library);
                    showPodcasts.startCommand(objectMapper, outputs);
                }
                case "printCurrentPage" -> {
                    PrintCurrentPage printCurrentPage = new PrintCurrentPage(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library);
                    printCurrentPage.startCommand(objectMapper, outputs);
                }
                case "changePage" -> {
                    ChangePage changePage = new ChangePage(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getNextPage(), library);
                    changePage.startCommand(objectMapper, outputs);
                }
                case "getAllUsers" -> {
                    GetAllUsers getAllUsers = new GetAllUsers(command.getCommand(),
                            command.getTimestamp(), library);
                    getAllUsers.startCommand(objectMapper, outputs);
                }
                case "deleteUser" -> {
                    DeleteUser deleteUser = new DeleteUser(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library,
                            players);
                    deleteUser.startCommand(objectMapper, outputs);
                }
                case "removeAlbum" -> {
                    RemoveAlbum removeAlbum = new RemoveAlbum(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library, players);
                    removeAlbum.startCommand(objectMapper, outputs);
                }
                case "getTop5Albums" -> {
                    GetTop5Albums getTop5Albums = new GetTop5Albums(command.getCommand(),
                            command.getTimestamp(), library);
                    getTop5Albums.startCommand(objectMapper, outputs);
                }
                case "getTop5Artists" -> {
                    GetTop5Artists getTop5Artists = new GetTop5Artists(
                            command.getCommand(), command.getTimestamp(), library);
                    getTop5Artists.startCommand(objectMapper, outputs);
                }
                case "removePodcast" -> {
                    RemovePodcast removePodcast = new RemovePodcast(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library, players);
                    removePodcast.startCommand(objectMapper, outputs);
                }
                case "wrapped" -> {
                    Wrapped wrapped = new Wrapped(command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, players);
                    wrapped.startCommand(objectMapper, outputs);
                }
                case "buyPremium" -> {
                    BuyPremium buyPremium = new BuyPremium(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    buyPremium.startCommand(objectMapper, outputs);
                }
                case "cancelPremium" -> {
                    CancelPremium cancelPremium = new CancelPremium(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                    cancelPremium.startCommand(objectMapper, outputs);
                }
                case "seeMerch" -> {
                    SeeMerch seeMerch = new SeeMerch(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                    seeMerch.startCommand(objectMapper, outputs);
                }
                case "buyMerch" -> {
                    BuyMerch buyMerch = new BuyMerch(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library);
                    buyMerch.startCommand(objectMapper, outputs);
                }
                case "subscribe" -> {
                    Subscribe subscribe =  new Subscribe(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                    subscribe.startCommand(objectMapper, outputs);
                }
                case "getNotifications" -> {
                    GetNotifications getNotifications = new GetNotifications(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                    getNotifications.startCommand(objectMapper, outputs);
                }
                default -> { }
            }
            // we get the last typestamp
            this.lastTimestamp = command.getTimestamp();
        }
        this.updateAllPlayers();
        this.endProgram();
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setOutputs(final ArrayNode outputs) {
        this.outputs = outputs;
    }

    public void setCommands(final List<Command> commands) {
        this.commands = commands;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }

    public void setTimestamp(final Integer timestamp) {
        this.lastTimestamp = timestamp;
    }

    public void setGlobalPlaylistId(final Integer globalPlaylistId) {
        this.globalPlaylistId = globalPlaylistId;
    }
}
