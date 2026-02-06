package ghost.ognotepad.frontend;

public sealed interface Code permits Code.Success, Code.Error, Code.Cancel {
    record Success(String payload) implements Code {}
    record Error(String error) implements Code {}
    record Cancel() implements Code {}
}
