package application.commands.root;

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
import application.commands.admin.recommendations.LoadRecommendation;
import application.commands.admin.recommendations.UpdateRecommendations;
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
import application.commands.users.normal.pages.NextPage;
import application.commands.users.normal.pages.PreviousPage;
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
            // for each command we create a new instance and we build the current command
            Commands currentCommand = null;
            switch (command.getCommand()) {
                case "search" -> {
                    currentCommand = new Search(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getType(), command.getFilters(), library, players);
                }
                case "select" -> {
                    currentCommand = new Select(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getItemNumber(), library);
                }
                case "load" -> {
                    currentCommand = new Load(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "playPause" -> {
                    currentCommand = new PlayPause(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library,
                            players);
                }
                case "repeat" -> {
                    currentCommand = new Repeat(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "shuffle" -> {
                    currentCommand = new Shuffle(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getSeed(), library, players);
                }
                case "forward" -> {
                    currentCommand = new Forward(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "backward" -> {
                    currentCommand = new Backward(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "like" -> {
                    currentCommand = new Like(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "next" -> {
                    currentCommand = new Next(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "prev" -> {
                    currentCommand = new Prev(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "addRemoveInPlaylist" -> {
                    currentCommand = new AddRemoveInPlaylist(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), command.getPlaylistId(),
                            library, players);
                }
                case "status" -> {
                    currentCommand = new Status(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "createPlaylist" -> {
                    currentCommand = new CreatePlaylist(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getPlaylistName(), globalPlaylistId, library);
                }
                case "switchVisibility" -> {
                    currentCommand = new SwitchVisibility(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), command.getPlaylistId(), library);
                }
                case "follow" -> {
                    currentCommand = new FollowPlaylist(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                }
                case "showPlaylists" -> {
                    currentCommand = new ShowPlaylists(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library);
                }
                case "showPreferredSongs" -> {
                    currentCommand = new ShowPreferredSongs(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library);
                }
                case "getTop5Songs" -> {
                    currentCommand = new GetTop5Songs(command.getCommand(),
                            command.getTimestamp(), library);
                }
                case "getTop5Playlists" -> {
                    currentCommand = new GetTop5Playlists(command.getCommand(),
                            command.getTimestamp(), library);
                }
                case "switchConnectionStatus" -> {
                    currentCommand = new SwitchConnectionStatus(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, players);
                }
                case "getOnlineUsers" -> {
                    currentCommand = new GetOnlineUsers(command.getCommand(),
                            command.getTimestamp(), library);
                }
                case "addUser" -> {
                    currentCommand = new AddUser(command.getCommand(), command.getTimestamp(),
                            command.getType(), library, command.getUsername(), command.getAge(),
                            command.getCity());
                }
                case "addAlbum" -> {
                    currentCommand = new AddAlbum(command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, command.getName(),
                            command.getReleaseYear(), command.getDescription(),
                            command.getSongs());
                }
                case "addEvent" -> {
                    currentCommand = new AddEvent(command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, command.getName(),
                            command.getDescription(), command.getDate());
                }
                case "removeEvent" -> {
                    currentCommand = new RemoveEvent(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library);
                }
                case "addMerch" -> {
                    currentCommand = new AddMerch(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, command.getName(), command.getDescription(),
                            command.getPrice());
                }
                case "addPodcast" -> {
                    currentCommand = new AddPodcast(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library,
                            command.getName(), command.getEpisodes(),
                            players);
                }
                case "addAnnouncement" -> {
                    currentCommand = new AddAnnouncement(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, command.getName(), command.getDescription());
                }
                case "removeAnnouncement" -> {
                    currentCommand = new RemoveAnnouncement(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), command.getName(), library);
                }
                case "showAlbums" -> {
                    currentCommand = new ShowAlbums(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library);
                }
                case "showPodcasts" -> {
                    currentCommand = new ShowPodcasts(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library);
                }
                case "printCurrentPage" -> {
                    currentCommand = new PrintCurrentPage(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library);
                }
                case "changePage" -> {
                    currentCommand = new ChangePage(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getNextPage(), library, players);
                }
                case "getAllUsers" -> {
                    currentCommand = new GetAllUsers(command.getCommand(),
                            command.getTimestamp(), library);
                }
                case "deleteUser" -> {
                    currentCommand = new DeleteUser(command.getCommand(),
                            command.getUsername(), command.getTimestamp(), library,
                            players);
                }
                case "removeAlbum" -> {
                    currentCommand = new RemoveAlbum(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library, players);
                }
                case "getTop5Albums" -> {
                    currentCommand = new GetTop5Albums(command.getCommand(),
                            command.getTimestamp(), library);
                }
                case "getTop5Artists" -> {
                    currentCommand = new GetTop5Artists(
                            command.getCommand(), command.getTimestamp(), library);
                }
                case "removePodcast" -> {
                    currentCommand = new RemovePodcast(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library, players);
                }
                case "wrapped" -> {
                    currentCommand = new Wrapped(command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, players);
                }
                case "buyPremium" -> {
                    currentCommand = new BuyPremium(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "cancelPremium" -> {
                    currentCommand = new CancelPremium(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "seeMerch" -> {
                    currentCommand = new SeeMerch(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                }
                case "buyMerch" -> {
                    currentCommand = new BuyMerch(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            command.getName(), library);
                }
                case "subscribe" -> {
                    currentCommand =  new Subscribe(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                }
                case "getNotifications" -> {
                    currentCommand = new GetNotifications(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library);
                }
                case "previousPage" -> {
                    currentCommand = new PreviousPage(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "nextPage" -> {
                    currentCommand = new NextPage(command.getCommand(),
                            command.getUsername(), command.getTimestamp(),
                            library, players);
                }
                case "updateRecommendations" -> {
                    currentCommand =  new UpdateRecommendations(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), command.getRecommendationType(),
                            library, players);
                }
                case "loadRecommendations" -> {
                    currentCommand = new LoadRecommendation(
                            command.getCommand(), command.getUsername(),
                            command.getTimestamp(), library, players);
                }
                default -> { }
            }
            // we get the last typestamp
            this.lastTimestamp = command.getTimestamp();
            // now that we build the command we build the invoker
            Invoker invoker = new Invoker(currentCommand, objectMapper, outputs);
            // we start the action from the invoker
            if (currentCommand != null) {
                invoker.action();
            }
            // we make the adjustments for the needing commands
            if (command.getCommand().equals("createPlaylist")) {
                CreatePlaylist createPlaylistCommand = (CreatePlaylist) currentCommand;
                if (library.typeOfUser(createPlaylistCommand.getUsername()) == 1
                        && library.getUser(createPlaylistCommand.getUsername()).isStatus()) {
                    globalPlaylistId = createPlaylistCommand.getGlobalPlaylistId();
                }
            }
        }
        // we get the application ready to end and calculate statistics
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
