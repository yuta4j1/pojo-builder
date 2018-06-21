package entry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import builder.PojoBuilder;
import builder.TableAccess;

public class Main {

    public static void main(String[] args) {

        Properties props = new Properties();
        final Path input = Paths.get(args[0]);

        try (InputStream stream = Files.newInputStream(input)) {

            props.load(stream);
            TableAccess access = new TableAccess(props);
            PojoBuilder builder = new PojoBuilder(access, args[1]);

            builder.writeTo(System.out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

}
