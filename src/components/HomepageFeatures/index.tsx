import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
    title: string;
    img: string;
    description: ReactNode;
};

const FeatureList: FeatureItem[] = [
    {
        title: 'Cercar Biblioteques de Barcelona',
        img: require('@site/static/img/LibrariesScreen.png').default,
        description: (
            <>
                Cercar biblioteques a la província de Barcelona i filtra les biblioteques en funció de diversos criteris
            </>
        ),
    },
    {
        title: 'Cerca a Aladí',
        img: require('@site/static/img/BookScreen.png').default,
        description: (
            <>
                Cerca el catàleg de la Xarxa de Biblioteques Municipals de la Diputació de Barcelona (Aladí) per trobar
                llibres, revistes i altres recursos.
            </>
        ),
    },
];

function Feature({title, img, description}: FeatureItem) {
    return (
        <div className={clsx('col col--6')}>
            <div className="text--center">
                <img className={styles.featureSvg} src={img}/>
            </div>
            <div className="text--center padding-horiz--md">
                <Heading as="h3">{title}</Heading>
                <p>{description}</p>
            </div>
        </div>
    );
}

export default function HomepageFeatures(): ReactNode {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
