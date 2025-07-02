import Layout from "@theme/Layout";

export default function Hello() {
    return (
        <Layout title="Privacitat">
            <div
                style={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '50vh',
                    fontSize: '20px',
                }}>
                <p>
                    Bibliotecat no emmagatzema las teves dades personals.
                </p>
            </div>
        </Layout>
    );
}