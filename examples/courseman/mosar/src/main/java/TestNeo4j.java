import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import java.io.FileWriter;
import java.io.IOException;

public class TestNeo4j {
    public static void main(String[] args) {
        // Replace the following values with your Neo4j AuraDB connection details
        Test test = new Test();
        test.writeGraphToFile();
    }
}

class Test {
    String uri = "neo4j+s://c5ec22c0.databases.neo4j.io";
    String username = "neo4j";
    String password = "JpuuXGX5dO6qavzkbJQTN3DrIuTHgOvpt58N2MfrGWA";

    void writeGraphToFile() {
        try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))) {
            FileWriter writer = new FileWriter(".\\test_gen_modules\\example.txt");
            getGraphFromNode(getFirstNode(driver), driver, writer, null);
            writer.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    Value getFirstNode(Driver driver) {
        try {
            try (Session session = driver.session()) {
                Result result = session.run("MATCH (n) WHERE n.name=\"SCCCourseMan\" RETURN n;");
                while (result.hasNext()) {
                    Record record = result.next();
                    return record.get("n");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    void getGraphFromNode(Value initNode, Driver driver, FileWriter writer, String prev_node) {
        if (initNode == null) {
            return;
        }
        try {
            try (Session session = driver.session()) {


                Result result = session.run(
                        "MATCH (n)\n" +
                        "WHERE n.name = '" + initNode.get("name").asString() + "'\n" +
                        "OPTIONAL MATCH (n)-[r]-(m)\n" +
                        "RETURN type(r) as rel, m;"
                );
                while (result.hasNext()) {
                    Record record = result.next();
                    Value relatedNode = record.get("m");
                    String test = relatedNode.get("name").asString();
                    Value relation = record.get("rel");
                    writer.write(initNode.get("name").asString() + " - [" + relation.asString() + "] - " + relatedNode.get("name").asString() + "\n");
                    if(
                            prev_node == null
                            || (
                                    !relatedNode.get("name").asString().equals(prev_node)
                                    && relatedNode.get("name").asString().equals(initNode.get("name").asString())
                            )
                    ) {
                        getGraphFromNode(relatedNode, driver, writer, initNode.get("name").asString());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
