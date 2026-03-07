function Footer(){
    return(
        <footer style={styles.footer}>
            <p>© 2026 Dalgona App. All rights reserved.</p>
        </footer>
    );
}

const styles = {
    footer: {
        textAlign: 'center',
        marginTop: 'auto', // This pushes the footer to the bottom
    }
};

export default Footer;