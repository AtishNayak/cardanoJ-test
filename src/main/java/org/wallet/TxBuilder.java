package org.wallet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class TxBuilder {
    public static String assets = "path-to-assets-folder";
    public static String keypath = "path-to-keys-folder";

    // Method to Build Transaction
    public static void buildTransaction(String name, String collateral, String txIn) throws IOException, InterruptedException {
        String pp = assets + "path-to-parameter.json-file" ;
        String body = assets + "path-to-file.txbody-file" ;
        String tx = assets + "path-to-file.tx" ;

        queryProtocolParameters(pp) ;
        buildTransactionBody(txIn, name, collateral, pp, body) ;
        signTransaction(name, body, tx) ;
        submitTransaction(tx) ;
    }
    private static void queryProtocolParameters(String pp) throws IOException, InterruptedException {
        executeCommand("cardano-cli", "query", "protocol-parameters", "--testnet-magic", "2", "--out-file", pp) ;
    }

    private static void buildTransactionBody(String txIn, String name, String collateral, String pp, String body) throws IOException, InterruptedException {
        executeCommand("cardano-cli", "transaction", "build", "babbage-era", "--testnet-magic", "2", "--tx-in", txIn, "--tx-in-script-file", assets + "path-to-x.plutus-file",
                "--tx-in-inline-datum-present", "--tx-in-redeemer-file", assets + "path-to-x.json-file", "--tx-in-collateral", collateral,
                "--change-address", readFile(keypath + "/" + name + ".addr"), "--protocol-params-file", pp, "--out-file", body) ;
    }

    private static void signTransaction(String name, String body, String tx) throws IOException, InterruptedException {
        executeCommand("cardano-cli", "transaction", "sign", "--tx-body-file", body, "--signing-key-file", keypath + "/" + name + ".skey", "--testnet-magic", "2", "--out-file", tx) ;
    }

    public static void submitTransaction(String tx) throws IOException, InterruptedException {
        executeCommand("cardano-cli","transaction", "submit", "--testnet-magic", "2", "--tx-file", tx) ;
    }

    private static void executeCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO() ;
        Process process = pb.start();
        int exitCode = process.waitFor();
        System.out.println("Exited with code : " + exitCode);
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder() ;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(TxBuilder.class.getResourceAsStream(filePath))))) {
            String line ;
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString() ;
    }

}
