package org.bouncycastle.jce.cert;

import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Set;

/**
 * An abstract class that performs one or more checks on an 
 * <code>X509Certificate</code>. <br />
 * <br />
 * A concrete implementation of the <code>PKIXCertPathChecker</code> class 
 * can be created to extend the PKIX certification path validation algorithm.
 * For example, an implementation may check for and process a critical private
 * extension of each certificate in a certification path.<br />
 * <br />
 * Instances of <code>PKIXCertPathChecker</code> are passed as parameters
 * using the {@link PKIXParameters#setCertPathCheckers setCertPathCheckers}
 * or {@link PKIXParameters#addCertPathChecker addCertPathChecker} methods 
 * of the <code>PKIXParameters</code> and <code>PKIXBuilderParameters</code>
 * class. Each of the <code>PKIXCertPathChecker</code>s {@link #check check}
 * methods will be called, in turn, for each certificate processed by a PKIX 
 * <code>CertPathValidator</code> or <code>CertPathBuilder</code> 
 * implementation.<br />
 * <br /> 
 * A <code>PKIXCertPathChecker</code> may be called multiple times on 
 * successive certificates in a certification path. Concrete subclasses
 * are expected to maintain any internal state that may be necessary to 
 * check successive certificates. The {@link #init init} method is used 
 * to initialize the internal state of the checker so that the certificates 
 * of a new certification path may be checked. A stateful implementation
 * <b>must</b> override the {@link #clone clone} method if necessary in 
 * order to allow a PKIX <code>CertPathBuilder</code> to efficiently 
 * backtrack and try other paths. In these situations, the 
 * <code>CertPathBuilder</code> is able to restore prior path validation 
 * states by restoring the cloned <code>PKIXCertPathChecker</code>s.<br />
 * <br />
 * The order in which the certificates are presented to the 
 * <code>PKIXCertPathChecker</code> may be either in the forward direction 
 * (from target to most-trusted CA) or in the reverse direction (from 
 * most-trusted CA to target). A <code>PKIXCertPathChecker</code> implementation
 * <b>must</b> support reverse checking (the ability to perform its checks when
 * it is presented with certificates in the reverse direction) and <b>may</b> 
 * support forward checking (the ability to perform its checks when it is 
 * presented with certificates in the forward direction). The 
 * {@link #isForwardCheckingSupported isForwardCheckingSupported} method 
 * indicates whether forward checking is supported.<br />
 * <br />
 * Additional input parameters required for executing the check may be 
 * specified through constructors of concrete implementations of this class.<br />
 * <br />
 * <b>Concurrent Access</b><br />
 * <br />
 * Unless otherwise specified, the methods defined in this class are not
 * thread-safe. Multiple threads that need to access a single
 * object concurrently should synchronize amongst themselves and
 * provide the necessary locking. Multiple threads each manipulating
 * separate objects need not synchronize.
 *
 * @see PKIXParameters 
 * @see PKIXBuilderParameters
 **/
public abstract class PKIXCertPathChecker implements Cloneable
{
    
    /**
     * Default constructor.
     */
    protected PKIXCertPathChecker() {}

    /**
     * Initializes the internal state of this <code>PKIXCertPathChecker</code>.
     * <p> 
     * The <code>forward</code> flag specifies the order that
     * certificates will be passed to the {@link #check check} method
     * (forward or reverse). A <code>PKIXCertPathChecker</code> <b>must</b> 
     * support reverse checking and <b>may</b> support forward checking. 
     *
     * @param forward the order that certificates are presented to
     * the <code>check</code> method. If <code>true</code>, certificates 
     * are presented from target to most-trusted CA (forward); if 
     * <code>false</code>, from most-trusted CA to target (reverse).
     * @exception CertPathValidatorException if this 
     * <code>PKIXCertPathChecker</code> is unable to check certificates in 
     * the specified order; it should never be thrown if the forward flag 
     * is false since reverse checking must be supported
     */
    public abstract void init(boolean forward)
    throws CertPathValidatorException;

    /**
     * Indicates if forward checking is supported. Forward checking refers
     * to the ability of the <code>PKIXCertPathChecker</code> to perform 
     * its checks when certificates are presented to the <code>check</code>
     * method in the forward direction (from target to most-trusted CA).
     *
     * @return <code>true</code> if forward checking is supported, 
     * <code>false</code> otherwise
     */
    public abstract boolean isForwardCheckingSupported();

    /**
     * Returns an immutable <code>Set</code> of X.509 certificate extensions 
     * that this <code>PKIXCertPathChecker</code> supports (i.e. recognizes, is 
     * able to process), or <code>null</code> if no extensions are supported. 
     * <p>
     * Each element of the set is a <code>String</code> representing the
     * Object Identifier (OID) of the X.509 extension that is supported.
     * The OID is represented by a set of nonnegative integers separated by
     * periods.
     * <p>
     * All X.509 certificate extensions that a <code>PKIXCertPathChecker</code>
     * might possibly be able to process should be included in the set.
     *
     * @return an immutable <code>Set</code> of X.509 extension OIDs (in
     * <code>String</code> format) supported by this 
     * <code>PKIXCertPathChecker</code>, or <code>null</code> if no 
     * extensions are supported
     */
    public abstract Set getSupportedExtensions();

    /**
     * Performs the check(s) on the specified certificate using its internal 
     * state and removes any critical extensions that it processes from the 
     * specified collection of OID strings that represent the unresolved 
     * critical extensions. The certificates are presented in the order 
     * specified by the <code>init</code> method.
     *
     * @param cert the <code>Certificate</code> to be checked
     * @param unresolvedCritExts a <code>Collection</code> of OID strings 
     * representing the current set of unresolved critical extensions
     * @exception CertPathValidatorException if the specified certificate does 
     * not pass the check
     */
    public abstract void check(
        Certificate cert,
        Collection unresolvedCritExts)
    throws CertPathValidatorException;

    /**
     * Returns a clone of this object. Calls the <code>Object.clone()</code>
     * method.
     * All subclasses which maintain state must support and
     * override this method, if necessary.
     * 
     * @return a copy of this <code>PKIXCertPathChecker</code>
     */
    public Object clone()
    {
    try {
        return super.clone();
    } catch ( CloneNotSupportedException ex ) {
        /* Cannot happen */
        throw new InternalError( ex.toString() );
    }
    }
}
