package net.zomis.duga

interface StackAPI {
    def apiCall(String apiCall, String site, String filter) throws IOException;
}
