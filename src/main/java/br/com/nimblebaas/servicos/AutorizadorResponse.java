package br.com.nimblebaas.servicos;

public class AutorizadorResponse {

    private String status;
    private Data data;

    public AutorizadorResponse() {
    }

    public AutorizadorResponse(String status, Data data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private boolean authorized;

        public Data() {
        }

        public Data(boolean authorized) {
            this.authorized = authorized;
        }

        public boolean isAuthorized() {
            return authorized;
        }

        public void setAuthorized(boolean authorized) {
            this.authorized = authorized;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "authorized=" + authorized +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AutorizadorResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
