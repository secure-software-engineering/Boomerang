package typestate;

import boomerang.scope.Statement;
import typestate.finiteautomata.Transition;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatementSequence {

    public static class Entry {

        private final Statement statement;
        private final Transition transition;

        public Entry(Statement statement, Transition transition) {
            this.statement = statement;
            this.transition = transition;
        }

        public Statement getStatement() {
            return statement;
        }

        public Transition getTransition() {
            return transition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return Objects.equals(statement, entry.statement) && Objects.equals(transition, entry.transition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(statement, transition);
        }

        @Override
        public String toString() {
            return "{" + statement + " [" + transition + "]}";
        }
    }

    private final List<Entry> sequence;

    public StatementSequence(Entry initialStmt) {
        this.sequence = List.of(initialStmt);
    }

    public StatementSequence(List<Entry> sequence) {
        this.sequence = List.copyOf(sequence);
    }

    public List<Entry> getSequence() {
        return sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementSequence that = (StatementSequence) o;
        return Objects.equals(sequence, that.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence);
    }

    @Override
    public String toString() {
        return sequence.stream().map(Object::toString).collect(Collectors.joining(" -> "));
    }
}
