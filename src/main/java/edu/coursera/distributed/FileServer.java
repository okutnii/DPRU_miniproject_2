package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        while (true) {
            Socket s = socket.accept();
            try(InputStream stream = s.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                    PrintWriter printer = new PrintWriter(s.getOutputStream())) {

                String line = bufferedReader.readLine();
                if (line != null && line.startsWith("GET")) {
                    String path = line.split(" ")[1];
                    String data = fs.readFile(new PCDPPath(path));
                    if (data != null) {
                        printer.write("HTTP/1.0 200 OK\r\n");
                        printer.write("\r\n");
                        printer.write("\r\n");
                        printer.write(data + "\r\n");
                    } else {
                        printer.write("HTTP/1.0 404 Not Found\r\n");
                        printer.write("\r\n");
                        printer.write("\r\n");
                    }
                } else {
                    printer.write("HTTP/1.0 400 Bad Request\r\n");
                    printer.write("\r\n");
                    printer.write("\r\n");
                }
            }

        }
    }
}
