import java.util.*;
public class LeaderboardDemo {
    static class Team {
        String name;
        int points;
        int goalDiff;
        Team(String name, int points, int goalDiff) {
            this.name = name;
            this.points = points;
            this.goalDiff = goalDiff;
        }
        @Override
        public String toString() {
            return name + "(" + points + ", "
                    + goalDiff + ")";
        }
    }
    static class Leaderboard<T> {
        private final List<T> items = new ArrayList<>();
        void add(T item) {
            items.add(item);
        }
        void addAll(List<? extends T> values) {
            items.addAll(values);
        }
        List<T> ranking(Comparator<? super T> comparator) {
            List<T> result = new ArrayList<>(items);
            result.sort(comparator);
            return result;
        }
        List<T> original() {
            return new ArrayList<>(items);
        }
    }
    public static void main(String[] args) {
        Comparator<Team> teamComparator =
                Comparator.comparingInt(
                        (Team t) -> t.points)
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        (Team t) -> t.goalDiff)
                                        .reversed())
                        .thenComparing(t -> t.name);

        Leaderboard<Team> board = new Leaderboard<>();

        board.add(new Team("Panthers", 9, 4));
        board.add(new Team("Tigers", 12, 5));
        board.add(new Team("Bisons", 9, 4));
        board.add(new Team("Eagles", 9, 6));

        System.out.println("Original order:");
        System.out.println(board.original());

        System.out.println("\nRanked order:");

        List<Team> ranked = board.ranking(teamComparator);

        int rank = 1;

        for (Team team : ranked) {
            System.out.println(rank + ". " + team);
            rank++;
        }

        System.out.println("\nOriginal order after sorting:");
        System.out.println(board.original());
    }
}
