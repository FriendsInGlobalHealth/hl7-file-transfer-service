package hl7.file.transfer.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.mail.MessagingException;
import java.io.*;


@Component
@RequiredArgsConstructor
public class Hl7FileDownloadTask {
    private static final Logger log = LoggerFactory.getLogger(Hl7FileDownloadTask.class);

    @Autowired
    private MailService mailService;

    @Value("${health.facility}")
    private String healthFacility;;
    @Value("${hl7.destination.file.name}")
    private String destinationFileName;
    @Value("${hl7.remote.file.path}")
    private String remoteFilePath;
    @Value("${remote.server.password}")
    private String password;

    @Value("${remote.port}")
    private String remotePort;

    //Todas as segundas feiras as 8h30min (O servidor tem duas horas a menos)
    //@Scheduled(cron = "0 27 8 * * MON")
    @Scheduled(cron = "0 1/2 * * * *")
    public void sendViralResultReport() throws MessagingException, UnsupportedEncodingException {
        log.info("Iniciando a task de Download de HL7");
        final String command="sshpass -p "+password+" scp -P "+remotePort+" "+remoteFilePath+destinationFileName+" /home/hl7";
          Process p=null;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        //Wait to get exit value
        try {
            p.waitFor();
            final int exitValue = p.waitFor();
            if (exitValue == 0)
                System.out.println("Successfully executed the command");
            else {
                System.out.println("Failed to execute the following command due to the following error(s):");
                StringBuilder sb=new StringBuilder("Erro ao tentar transferir ficheiro -> Fichiro nao encontrado");
                try (final BufferedReader b = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                    String line;
                    if ((line = b.readLine()) != null) {
                        System.out.println(line);
                        //sb.append(line);
                    }
                    mailService.sendEmail(sb.toString(),healthFacility);

                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
