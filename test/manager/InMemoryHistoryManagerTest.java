package manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }
}
