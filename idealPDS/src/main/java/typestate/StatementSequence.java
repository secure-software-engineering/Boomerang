package typestate;

import boomerang.scope.Statement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatementSequence {

    private final List<Statement> sequence;

    public StatementSequence(Statement initialStmt) {
        this.sequence = List.of(initialStmt);
    }

    public StatementSequence(List<Statement> sequence) {
        this.sequence = List.copyOf(sequence);
    }

    public List<Statement> getSequence() {
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
