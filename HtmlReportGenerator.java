import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HtmlReportGenerator {

    public static void generateOfflineDashboard(String projectName, List<String> remediatedDeps, List<String> manualReviewDeps, String outputPath) {
        StringBuilder html = new StringBuilder();

        // 1. Inject the HTML Head and Inline CSS (100% Offline)
        html.append("<!DOCTYPE html>\n<html lang='en'>\n<head>\n")
            .append("<meta charset='UTF-8'>\n")
            .append("<title>Security Scan Report - ").append(projectName).append("</title>\n")
            .append("<style>\n")
            .append("  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #1e1e1e; color: #d4d4d4; margin: 0; padding: 20px; }\n")
            .append("  .container { max-width: 1200px; margin: 0 auto; }\n")
            .append("  h1 { border-bottom: 2px solid #007acc; padding-bottom: 10px; color: #ffffff; }\n")
            .append("  .summary-cards { display: flex; gap: 20px; margin-bottom: 30px; }\n")
            .append("  .card { background-color: #252526; padding: 20px; border-radius: 8px; flex: 1; text-align: center; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }\n")
            .append("  .card h2 { margin: 0; font-size: 36px; }\n")
            .append("  .text-danger { color: #f14c4c; }\n")
            .append("  .text-success { color: #89d185; }\n")
            .append("  .text-warning { color: #cca700; }\n")
            .append("  table { width: 100%; border-collapse: collapse; margin-bottom: 30px; background-color: #252526; box-shadow: 0 4px 6px rgba(0,0,0,0.3); }\n")
            .append("  th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #3e3e42; }\n")
            .append("  th { background-color: #333333; color: #ffffff; }\n")
            .append("  code { background-color: #1e1e1e; padding: 2px 6px; border-radius: 4px; font-family: Consolas, monospace; color: #ce9178; }\n")
            .append("  .badge { padding: 4px 8px; border-radius: 4px; font-weight: bold; font-size: 12px; }\n")
            .append("  .badge-fixed { background-color: #1e4620; color: #89d185; border: 1px solid #89d185; }\n")
            .append("  .badge-vuln { background-color: #4d1212; color: #f14c4c; border: 1px solid #f14c4c; }\n")
            .append("</style>\n</head>\n<body>\n");

        // 2. Build the Executive Summary
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        html.append("<div class='container'>\n")
            .append("<h1>Security Scan: <code>").append(projectName).append("</code></h1>\n")
            .append("<p>Generated on: ").append(timestamp).append("</p>\n");

        html.append("<div class='summary-cards'>\n")
            .append("  <div class='card'><h2 class='text-danger'>").append(manualReviewDeps.size()).append("</h2><p>Requires Manual Review</p></div>\n")
            .append("  <div class='card'><h2 class='text-success'>").append(remediatedDeps.size()).append("</h2><p>Autonomously Fixed</p></div>\n")
            .append("</div>\n");

        // 3. Autonomously Upgraded Section
        html.append("<h3><span class='text-success'>&#10004;</span> Autonomously Upgraded & Remediated</h3>\n");
        if (remediatedDeps.isEmpty()) {
            html.append("<p>No dependencies were automatically fixed in this run.</p>\n");
        } else {
            html.append("<table>\n<tr><th>Dependency</th><th>Action Taken</th><th>Status</th></tr>\n");
            for (String dep : remediatedDeps) {
                // TODO: Replace with your actual DTO getters (e.g., dep.getArtifactId(), dep.getNewVersion())
                html.append("<tr>")
                    .append("<td><code>").append(dep).append("</code></td>")
                    .append("<td>OpenRewrite Recipe Executed / Version Bumped</td>")
                    .append("<td><span class='badge badge-fixed'>RESOLVED</span></td>")
                    .append("</tr>\n");
            }
            html.append("</table>\n");
        }

        // 4. Manual Review Section
        html.append("<h3><span class='text-danger'>&#9888;</span> Requires Manual Review (Transitives / No Recipe)</h3>\n");
        if (manualReviewDeps.isEmpty()) {
            html.append("<p>No outstanding vulnerabilities!</p>\n");
        } else {
            html.append("<table>\n<tr><th>Dependency</th><th>Current Version</th><th>CVE Details</th></tr>\n");
            for (String dep : manualReviewDeps) {
                // TODO: Replace with your actual DTO getters
                html.append("<tr>")
                    .append("<td><code>").append(dep).append("</code></td>")
                    .append("<td>Vulnerable Version</td>")
                    .append("<td><span class='badge badge-vuln'>UNPATCHED</span> Check Maven Tree</td>")
                    .append("</tr>\n");
            }
            html.append("</table>\n");
        }

        // 5. Close out the HTML
        html.append("</div>\n</body>\n</html>");

        // 6. Write to File
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(html.toString());
            System.out.println("[INFO] Generated HTML Dashboard at: " + outputPath);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to write HTML report: " + e.getMessage());
        }
    }
}
