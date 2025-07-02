import Layout from "@theme/Layout";

export default function Hello() {
    return (
        <Layout title="Privacitat">
            <div
                style={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    fontSize: '20px',
                    paddingLeft: '10vw',
                    paddingRight: '10vw',
                    paddingTop: '10vh',
                    paddingBottom: '10vh',
                }}>
                <p style={{textAlign: "justify"}}>
                    Bibliotecat no emmagatzema las teves dades personals. Es una aplicacion de codi obert que permet als
                    usuaris gestionar les seves biblioteques de manera privada i segura. No es guarden dades
                    localment al teu dispositiu. Això garanteix la teva privacitat i seguretat en tot moment.
                </p>
            </div>
        </Layout>
    );
}