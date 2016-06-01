/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner.java;

import java.util.ArrayList;
import java.util.List;

public class BuiltInTypes
{

   private static final String raw_string =
            "AbstractAction AbstractBorder AbstractButton AbstractCellEditor AbstractCollection\n"
                     +
                     "AbstractColorChooserPanel AbstractDocument AbstractExecutorService AbstractInterruptibleChannel\n"
                     +
                     "AbstractLayoutCache AbstractList AbstractListModel AbstractMap AbstractMethodError AbstractPreferences\n"
                     +
                     "AbstractQueue AbstractQueuedSynchronizer AbstractSelectableChannel AbstractSelectionKey AbstractSelector\n"
                     +
                     "AbstractSequentialList AbstractSet AbstractSpinnerModel AbstractTableModel AbstractUndoableEdit\n"
                     +
                     "AbstractWriter AccessControlContext AccessControlException AccessController AccessException Accessible\n"
                     +
                     "AccessibleAction AccessibleAttributeSequence AccessibleBundle AccessibleComponent AccessibleContext\n"
                     +
                     "AccessibleEditableText AccessibleExtendedComponent AccessibleExtendedTable AccessibleExtendedText\n"
                     +
                     "AccessibleHyperlink AccessibleHypertext AccessibleIcon AccessibleKeyBinding AccessibleObject\n"
                     +
                     "AccessibleRelation AccessibleRelationSet AccessibleResourceBundle AccessibleRole AccessibleSelection\n"
                     +
                     "AccessibleState AccessibleStateSet AccessibleStreamable AccessibleTable AccessibleTableModelChange\n"
                     +
                     "AccessibleText AccessibleTextSequence AccessibleValue AccountException AccountExpiredException\n"
                     +
                     "AccountLockedException AccountNotFoundException Acl AclEntry AclNotFoundException Action ActionEvent\n"
                     +
                     "ActionListener ActionMap ActionMapUIResource Activatable ActivateFailedException ActivationDesc\n"
                     +
                     "ActivationException ActivationGroup ActivationGroupDesc ActivationGroupID ActivationGroup_Stub\n"
                     +
                     "ActivationID ActivationInstantiator ActivationMonitor ActivationSystem Activator ActiveEvent\n"
                     +
                     "ActivityCompletedException ActivityRequiredException Adjustable AdjustmentEvent AdjustmentListener\n"
                     +
                     "Adler32 AffineTransform AffineTransformOp AlgorithmParameterGenerator AlgorithmParameterGeneratorSpi\n"
                     +
                     "AlgorithmParameters AlgorithmParameterSpec AlgorithmParametersSpi AllPermission AlphaComposite\n"
                     +
                     "AlreadyBoundException AlreadyConnectedException AncestorEvent AncestorListener AnnotatedElement\n"
                     +
                     "Annotation AnnotationFormatError AnnotationTypeMismatchException AppConfigurationEntry Appendable Applet\n"
                     +
                     "AppletContext AppletInitializer AppletStub Arc2D Area AreaAveragingScaleFilter ArithmeticException Array\n"
                     +
                     "ArrayBlockingQueue ArrayIndexOutOfBoundsException ArrayList Arrays ArrayStoreException ArrayType\n"
                     +
                     "AssertionError AsyncBoxView AsynchronousCloseException AtomicBoolean AtomicInteger AtomicIntegerArray\n"
                     +
                     "AtomicIntegerFieldUpdater AtomicLong AtomicLongArray AtomicLongFieldUpdater AtomicMarkableReference\n"
                     +
                     "AtomicReference AtomicReferenceArray AtomicReferenceFieldUpdater AtomicStampedReference Attribute\n"
                     +
                     "AttributeChangeNotification AttributeChangeNotificationFilter AttributedCharacterIterator\n"
                     +
                     "AttributedString AttributeException AttributeInUseException AttributeList AttributeModificationException\n"
                     +
                     "AttributeNotFoundException Attributes AttributeSet AttributeSetUtilities AttributeValueExp AudioClip\n"
                     +
                     "AudioFileFormat AudioFileReader AudioFileWriter AudioFormat AudioInputStream AudioPermission AudioSystem\n"
                     +
                     "AuthenticationException AuthenticationNotSupportedException Authenticator AuthorizeCallback\n"
                     +
                     "AuthPermission AuthProvider Autoscroll AWTError AWTEvent AWTEventListener AWTEventListenerProxy\n"
                     +
                     "AWTEventMulticaster AWTException AWTKeyStroke AWTPermission BackingStoreException\n"
                     +
                     "BadAttributeValueExpException BadBinaryOpValueExpException BadLocationException BadPaddingException\n"
                     +
                     "BadStringOperationException BandCombineOp BandedSampleModel BaseRowSet BasicArrowButton BasicAttribute\n"
                     +
                     "BasicAttributes BasicBorders BasicButtonListener BasicButtonUI BasicCheckBoxMenuItemUI BasicCheckBoxUI\n"
                     +
                     "BasicColorChooserUI BasicComboBoxEditor BasicComboBoxRenderer BasicComboBoxUI BasicComboPopup\n"
                     +
                     "BasicControl BasicDesktopIconUI BasicDesktopPaneUI BasicDirectoryModel BasicEditorPaneUI\n"
                     +
                     "BasicFileChooserUI BasicFormattedTextFieldUI BasicGraphicsUtils BasicHTML BasicIconFactory\n"
                     +
                     "BasicInternalFrameTitlePane BasicInternalFrameUI BasicLabelUI BasicListUI BasicLookAndFeel\n"
                     +
                     "BasicMenuBarUI BasicMenuItemUI BasicMenuUI BasicOptionPaneUI BasicPanelUI BasicPasswordFieldUI\n"
                     +
                     "BasicPermission BasicPopupMenuSeparatorUI BasicPopupMenuUI BasicProgressBarUI BasicRadioButtonMenuItemUI\n"
                     +
                     "BasicRadioButtonUI BasicRootPaneUI BasicScrollBarUI BasicScrollPaneUI BasicSeparatorUI BasicSliderUI\n"
                     +
                     "BasicSpinnerUI BasicSplitPaneDivider BasicSplitPaneUI BasicStroke BasicTabbedPaneUI BasicTableHeaderUI\n"
                     +
                     "BasicTableUI BasicTextAreaUI BasicTextFieldUI BasicTextPaneUI BasicTextUI BasicToggleButtonUI\n"
                     +
                     "BasicToolBarSeparatorUI BasicToolBarUI BasicToolTipUI BasicTreeUI BasicViewportUI BatchUpdateException\n"
                     +
                     "BeanContext BeanContextChild BeanContextChildComponentProxy BeanContextChildSupport\n"
                     +
                     "BeanContextContainerProxy BeanContextEvent BeanContextMembershipEvent BeanContextMembershipListener\n"
                     +
                     "BeanContextProxy BeanContextServiceAvailableEvent BeanContextServiceProvider\n"
                     +
                     "BeanContextServiceProviderBeanInfo BeanContextServiceRevokedEvent BeanContextServiceRevokedListener\n"
                     +
                     "BeanContextServices BeanContextServicesListener BeanContextServicesSupport BeanContextSupport\n"
                     +
                     "BeanDescriptor BeanInfo Beans BevelBorder Bidi BigDecimal BigInteger BinaryRefAddr BindException Binding\n"
                     +
                     "BitSet Blob BlockingQueue BlockView BMPImageWriteParam Book Boolean BooleanControl Border BorderFactory\n"
                     +
                     "BorderLayout BorderUIResource BoundedRangeModel Box BoxLayout BoxView BreakIterator\n"
                     +
                     "BrokenBarrierException Buffer BufferCapabilities BufferedImage BufferedImageFilter BufferedImageOp\n"
                     +
                     "BufferedInputStream BufferedOutputStream BufferedReader BufferedWriter BufferOverflowException\n"
                     +
                     "BufferStrategy BufferUnderflowException Button ButtonGroup ButtonModel ButtonUI Byte\n"
                     +
                     "ByteArrayInputStream ByteArrayOutputStream ByteBuffer ByteChannel ByteLookupTable ByteOrder CachedRowSet\n"
                     +
                     "CacheRequest CacheResponse Calendar Callable CallableStatement Callback CallbackHandler\n"
                     +
                     "CancelablePrintJob CancellationException CancelledKeyException CannotProceedException\n"
                     +
                     "CannotRedoException CannotUndoException Canvas CardLayout Caret CaretEvent CaretListener CellEditor\n"
                     +
                     "CellEditorListener CellRendererPane Certificate CertificateEncodingException CertificateException\n"
                     +
                     "CertificateExpiredException CertificateFactory CertificateFactorySpi CertificateNotYetValidException\n"
                     +
                     "CertificateParsingException CertPath CertPathBuilder CertPathBuilderException CertPathBuilderResult\n"
                     +
                     "CertPathBuilderSpi CertPathParameters CertPathTrustManagerParameters CertPathValidator\n"
                     +
                     "CertPathValidatorException CertPathValidatorResult CertPathValidatorSpi CertSelector CertStore\n"
                     +
                     "CertStoreException CertStoreParameters CertStoreSpi ChangedCharSetException ChangeEvent ChangeListener\n"
                     +
                     "Channel Channels Character CharacterCodingException CharacterIterator CharArrayReader CharArrayWriter\n"
                     +
                     "CharBuffer CharConversionException CharSequence Charset CharsetDecoder CharsetEncoder CharsetProvider\n"
                     +
                     "Checkbox CheckboxGroup CheckboxMenuItem CheckedInputStream CheckedOutputStream Checksum Choice\n"
                     +
                     "ChoiceCallback ChoiceFormat Chromaticity Cipher CipherInputStream CipherOutputStream CipherSpi Class\n"
                     +
                     "ClassCastException ClassCircularityError ClassDefinition ClassDesc ClassFileTransformer ClassFormatError\n"
                     +
                     "ClassLoader ClassLoaderRepository ClassLoadingMXBean ClassNotFoundException Clip Clipboard\n"
                     +
                     "ClipboardOwner Clob Cloneable CloneNotSupportedException Closeable ClosedByInterruptException\n"
                     +
                     "ClosedChannelException ClosedSelectorException CMMException CoderMalfunctionError CoderResult CodeSigner\n"
                     +
                     "CodeSource CodingErrorAction CollationElementIterator CollationKey Collator Collection\n"
                     +
                     "CollectionCertStoreParameters Collections Color ColorChooserComponentFactory ColorChooserUI\n"
                     +
                     "ColorConvertOp ColorModel ColorSelectionModel ColorSpace ColorSupported ColorType ColorUIResource\n"
                     +
                     "ComboBoxEditor ComboBoxModel ComboBoxUI ComboPopup CommunicationException Comparable Comparator\n"
                     +
                     "CompilationMXBean Compiler CompletionService Component ComponentAdapter ComponentColorModel\n"
                     +
                     "ComponentEvent ComponentInputMap ComponentInputMapUIResource ComponentListener ComponentOrientation\n"
                     +
                     "ComponentSampleModel ComponentUI ComponentView Composite CompositeContext CompositeData\n"
                     +
                     "CompositeDataSupport CompositeName CompositeType CompositeView CompoundBorder CompoundControl\n"
                     +
                     "CompoundEdit CompoundName Compression ConcurrentHashMap ConcurrentLinkedQueue ConcurrentMap\n"
                     +
                     "ConcurrentModificationException Condition Configuration ConfigurationException ConfirmationCallback\n"
                     +
                     "ConnectException ConnectIOException Connection ConnectionEvent ConnectionEventListener\n"
                     +
                     "ConnectionPendingException ConnectionPoolDataSource ConsoleHandler Constructor Container\n"
                     +
                     "ContainerAdapter ContainerEvent ContainerListener ContainerOrderFocusTraversalPolicy ContentHandler\n"
                     +
                     "ContentHandlerFactory ContentModel Context ContextNotEmptyException ContextualRenderedImageFactory\n"
                     +
                     "Control ControlFactory ControllerEventListener ConvolveOp CookieHandler Copies CopiesSupported\n"
                     +
                     "CopyOnWriteArrayList CopyOnWriteArraySet CountDownLatch CounterMonitor CounterMonitorMBean CRC32\n"
                     +
                     "CredentialException CredentialExpiredException CredentialNotFoundException CRL CRLException CRLSelector\n"
                     +
                     "CropImageFilter CSS CubicCurve2D Currency Cursor Customizer CyclicBarrier DatabaseMetaData DataBuffer\n"
                     +
                     "DataBufferByte DataBufferDouble DataBufferFloat DataBufferInt DataBufferShort DataBufferUShort\n"
                     +
                     "DataFlavor DataFormatException DatagramChannel DatagramPacket DatagramSocket DatagramSocketImpl\n"
                     +
                     "DatagramSocketImplFactory DataInput DataInputStream DataLine DataOutput DataOutputStream DataSource\n"
                     +
                     "DataTruncation DatatypeConfigurationException DatatypeConstants DatatypeFactory Date DateFormat\n"
                     +
                     "DateFormatSymbols DateFormatter DateTimeAtCompleted DateTimeAtCreation DateTimeAtProcessing\n"
                     +
                     "DateTimeSyntax DebugGraphics DecimalFormat DecimalFormatSymbols DefaultBoundedRangeModel\n"
                     +
                     "DefaultButtonModel DefaultCaret DefaultCellEditor DefaultColorSelectionModel DefaultComboBoxModel\n"
                     +
                     "DefaultDesktopManager DefaultEditorKit DefaultFocusManager DefaultFocusTraversalPolicy DefaultFormatter\n"
                     +
                     "DefaultFormatterFactory DefaultHighlighter DefaultKeyboardFocusManager DefaultListCellRenderer\n"
                     +
                     "DefaultListModel DefaultListSelectionModel DefaultLoaderRepository DefaultMenuLayout DefaultMetalTheme\n"
                     +
                     "DefaultMutableTreeNode DefaultPersistenceDelegate DefaultSingleSelectionModel DefaultStyledDocument\n"
                     +
                     "DefaultTableCellRenderer DefaultTableColumnModel DefaultTableModel DefaultTextUI DefaultTreeCellEditor\n"
                     +
                     "DefaultTreeCellRenderer DefaultTreeModel DefaultTreeSelectionModel Deflater DeflaterOutputStream Delayed\n"
                     +
                     "DelayQueue DelegationPermission Deprecated Descriptor DescriptorAccess DescriptorSupport DESedeKeySpec\n"
                     +
                     "DesignMode DESKeySpec DesktopIconUI DesktopManager DesktopPaneUI Destination Destroyable\n"
                     +
                     "DestroyFailedException DGC DHGenParameterSpec DHKey DHParameterSpec DHPrivateKey DHPrivateKeySpec\n"
                     +
                     "DHPublicKey DHPublicKeySpec Dialog Dictionary DigestException DigestInputStream DigestOutputStream\n"
                     +
                     "Dimension Dimension2D DimensionUIResource DirContext DirectColorModel DirectoryManager DirObjectFactory\n"
                     +
                     "DirStateFactory DisplayMode DnDConstants Doc DocAttribute DocAttributeSet DocFlavor DocPrintJob Document\n"
                     +
                     "DocumentBuilder DocumentBuilderFactory Documented DocumentEvent DocumentFilter DocumentListener\n"
                     +
                     "DocumentName DocumentParser DomainCombiner DOMLocator DOMResult DOMSource Double DoubleBuffer\n"
                     +
                     "DragGestureEvent DragGestureListener DragGestureRecognizer DragSource DragSourceAdapter\n"
                     +
                     "DragSourceContext DragSourceDragEvent DragSourceDropEvent DragSourceEvent DragSourceListener\n"
                     +
                     "DragSourceMotionListener Driver DriverManager DriverPropertyInfo DropTarget DropTargetAdapter\n"
                     +
                     "DropTargetContext DropTargetDragEvent DropTargetDropEvent DropTargetEvent DropTargetListener DSAKey\n"
                     +
                     "DSAKeyPairGenerator DSAParameterSpec DSAParams DSAPrivateKey DSAPrivateKeySpec DSAPublicKey\n"
                     +
                     "DSAPublicKeySpec DTD DTDConstants DuplicateFormatFlagsException Duration DynamicMBean ECField ECFieldF2m\n"
                     +
                     "ECFieldFp ECGenParameterSpec ECKey ECParameterSpec ECPoint ECPrivateKey ECPrivateKeySpec ECPublicKey\n"
                     +
                     "ECPublicKeySpec EditorKit Element ElementIterator ElementType Ellipse2D EllipticCurve EmptyBorder\n"
                     +
                     "EmptyStackException EncodedKeySpec Encoder EncryptedPrivateKeyInfo Entity Enum\n"
                     +
                     "EnumConstantNotPresentException EnumControl Enumeration EnumMap EnumSet EnumSyntax EOFException Error\n"
                     +
                     "ErrorListener ErrorManager EtchedBorder Event EventContext EventDirContext EventHandler EventListener\n"
                     +
                     "EventListenerList EventListenerProxy EventObject EventQueue EventSetDescriptor Exception\n"
                     +
                     "ExceptionInInitializerError ExceptionListener Exchanger ExecutionException Executor\n"
                     +
                     "ExecutorCompletionService Executors ExecutorService ExemptionMechanism ExemptionMechanismException\n"
                     +
                     "ExemptionMechanismSpi ExpandVetoException ExportException Expression ExtendedRequest ExtendedResponse\n"
                     +
                     "Externalizable FactoryConfigurationError FailedLoginException FeatureDescriptor Fidelity Field\n"
                     +
                     "FieldPosition FieldView File FileCacheImageInputStream FileCacheImageOutputStream FileChannel\n"
                     +
                     "FileChooserUI FileDescriptor FileDialog FileFilter FileHandler FileImageInputStream\n"
                     +
                     "FileImageOutputStream FileInputStream FileLock FileLockInterruptionException FilenameFilter FileNameMap\n"
                     +
                     "FileNotFoundException FileOutputStream FilePermission FileReader FileSystemView FileView FileWriter\n"
                     +
                     "Filter FilteredImageSource FilteredRowSet FilterInputStream FilterOutputStream FilterReader FilterWriter\n"
                     +
                     "Finishings FixedHeightLayoutCache FlatteningPathIterator FlavorEvent FlavorException FlavorListener\n"
                     +
                     "FlavorMap FlavorTable Float FloatBuffer FloatControl FlowLayout FlowView Flushable FocusAdapter\n"
                     +
                     "FocusEvent FocusListener FocusManager FocusTraversalPolicy Font FontFormatException FontMetrics\n"
                     +
                     "FontRenderContext FontUIResource Format FormatConversionProvider FormatFlagsConversionMismatchException\n"
                     +
                     "Formattable FormattableFlags Formatter FormatterClosedException FormSubmitEvent FormView Frame Future\n"
                     +
                     "FutureTask GapContent GarbageCollectorMXBean GatheringByteChannel GaugeMonitor GaugeMonitorMBean\n"
                     +
                     "GeneralPath GeneralSecurityException GenericArrayType GenericDeclaration GenericSignatureFormatError\n"
                     +
                     "GlyphJustificationInfo GlyphMetrics GlyphVector GlyphView GradientPaint GraphicAttribute Graphics\n"
                     +
                     "Graphics2D GraphicsConfigTemplate GraphicsConfiguration GraphicsDevice GraphicsEnvironment GrayFilter\n"
                     +
                     "GregorianCalendar GridBagConstraints GridBagLayout GridLayout Group Guard GuardedObject GZIPInputStream\n"
                     +
                     "GZIPOutputStream Handler HandshakeCompletedEvent HandshakeCompletedListener HasControls HashAttributeSet\n"
                     +
                     "HashDocAttributeSet HashMap HashPrintJobAttributeSet HashPrintRequestAttributeSet\n"
                     +
                     "HashPrintServiceAttributeSet HashSet Hashtable HeadlessException HierarchyBoundsAdapter\n"
                     +
                     "HierarchyBoundsListener HierarchyEvent HierarchyListener Highlighter HostnameVerifier HTML HTMLDocument\n"
                     +
                     "HTMLEditorKit HTMLFrameHyperlinkEvent HTMLWriter HttpRetryException HttpsURLConnection HttpURLConnection\n"
                     +
                     "HyperlinkEvent HyperlinkListener ICC_ColorSpace ICC_Profile ICC_ProfileGray ICC_ProfileRGB Icon\n"
                     +
                     "IconUIResource IconView Identity IdentityHashMap IdentityScope IIOByteBuffer IIOException IIOImage\n"
                     +
                     "IIOInvalidTreeException IIOMetadata IIOMetadataController IIOMetadataFormat IIOMetadataFormatImpl\n"
                     +
                     "IIOMetadataNode IIOParam IIOParamController IIOReadProgressListener IIOReadUpdateListener\n"
                     +
                     "IIOReadWarningListener IIORegistry IIOServiceProvider IIOWriteProgressListener IIOWriteWarningListener\n"
                     +
                     "IllegalAccessError IllegalAccessException IllegalArgumentException IllegalBlockingModeException\n"
                     +
                     "IllegalBlockSizeException IllegalCharsetNameException IllegalClassFormatException\n"
                     +
                     "IllegalComponentStateException IllegalFormatCodePointException IllegalFormatConversionException\n"
                     +
                     "IllegalFormatException IllegalFormatFlagsException IllegalFormatPrecisionException\n"
                     +
                     "IllegalFormatWidthException IllegalMonitorStateException IllegalPathStateException\n"
                     +
                     "IllegalSelectorException IllegalStateException IllegalThreadStateException Image ImageCapabilities\n"
                     +
                     "ImageConsumer ImageFilter ImageGraphicAttribute ImageIcon ImageInputStream ImageInputStreamImpl\n"
                     +
                     "ImageInputStreamSpi ImageIO ImageObserver ImageOutputStream ImageOutputStreamImpl ImageOutputStreamSpi\n"
                     +
                     "ImageProducer ImageReader ImageReaderSpi ImageReaderWriterSpi ImageReadParam ImageTranscoder\n"
                     +
                     "ImageTranscoderSpi ImageTypeSpecifier ImageView ImageWriteParam ImageWriter ImageWriterSpi\n"
                     +
                     "ImagingOpException IncompatibleClassChangeError IncompleteAnnotationException IndexColorModel\n"
                     +
                     "IndexedPropertyChangeEvent IndexedPropertyDescriptor IndexOutOfBoundsException Inet4Address Inet6Address\n"
                     +
                     "InetAddress InetSocketAddress Inflater InflaterInputStream InheritableThreadLocal Inherited\n"
                     +
                     "InitialContext InitialContextFactory InitialContextFactoryBuilder InitialDirContext InitialLdapContext\n"
                     +
                     "InlineView InputContext InputEvent InputMap InputMapUIResource InputMethod InputMethodContext\n"
                     +
                     "InputMethodDescriptor InputMethodEvent InputMethodHighlight InputMethodListener InputMethodRequests\n"
                     +
                     "InputMismatchException InputStream InputStreamReader InputSubset InputVerifier Insets InsetsUIResource\n"
                     +
                     "InstanceAlreadyExistsException InstanceNotFoundException InstantiationError InstantiationException\n"
                     +
                     "Instrument Instrumentation InsufficientResourcesException IntBuffer Integer IntegerSyntax InternalError\n"
                     +
                     "InternalFrameAdapter InternalFrameEvent InternalFrameFocusTraversalPolicy InternalFrameListener\n"
                     +
                     "InternalFrameUI InternationalFormatter InterruptedException InterruptedIOException\n"
                     +
                     "InterruptedNamingException InterruptibleChannel IntrospectionException Introspector\n"
                     +
                     "InvalidActivityException InvalidAlgorithmParameterException InvalidApplicationException\n"
                     +
                     "InvalidAttributeIdentifierException InvalidAttributesException InvalidAttributeValueException\n"
                     +
                     "InvalidClassException InvalidDnDOperationException InvalidKeyException InvalidKeySpecException\n"
                     +
                     "InvalidMarkException InvalidMidiDataException InvalidNameException InvalidObjectException\n"
                     +
                     "InvalidOpenTypeException InvalidParameterException InvalidParameterSpecException\n"
                     +
                     "InvalidPreferencesFormatException InvalidPropertiesFormatException InvalidRelationIdException\n"
                     +
                     "InvalidRelationServiceException InvalidRelationTypeException InvalidRoleInfoException\n"
                     +
                     "InvalidRoleValueException InvalidSearchControlsException InvalidSearchFilterException\n"
                     +
                     "InvalidTargetObjectTypeException InvalidTransactionException InvocationEvent InvocationHandler\n"
                     +
                     "InvocationTargetException IOException ItemEvent ItemListener ItemSelectable Iterable Iterator\n"
                     +
                     "IvParameterSpec JApplet JarEntry JarException JarFile JarInputStream JarOutputStream JarURLConnection\n"
                     +
                     "JButton JCheckBox JCheckBoxMenuItem JColorChooser JComboBox JComponent JdbcRowSet JDesktopPane JDialog\n"
                     +
                     "JEditorPane JFileChooser JFormattedTextField JFrame JInternalFrame JLabel JLayeredPane JList JMenu\n"
                     +
                     "JMenuBar JMenuItem JMException JMRuntimeException JMXAuthenticator JMXConnectionNotification\n"
                     +
                     "JMXConnector JMXConnectorFactory JMXConnectorProvider JMXConnectorServer JMXConnectorServerFactory\n"
                     +
                     "JMXConnectorServerMBean JMXConnectorServerProvider JMXPrincipal JMXProviderException\n"
                     +
                     "JMXServerErrorException JMXServiceURL JobAttributes JobHoldUntil JobImpressions JobImpressionsCompleted\n"
                     +
                     "JobImpressionsSupported JobKOctets JobKOctetsProcessed JobKOctetsSupported JobMediaSheets\n"
                     +
                     "JobMediaSheetsCompleted JobMediaSheetsSupported JobMessageFromOperator JobName JobOriginatingUserName\n"
                     +
                     "JobPriority JobPrioritySupported JobSheets JobState JobStateReason JobStateReasons Joinable JoinRowSet\n"
                     +
                     "JOptionPane JPanel JPasswordField JPEGHuffmanTable JPEGImageReadParam JPEGImageWriteParam JPEGQTable\n"
                     +
                     "JPopupMenu JProgressBar JRadioButton JRadioButtonMenuItem JRootPane JScrollBar JScrollPane JSeparator\n"
                     +
                     "JSlider JSpinner JSplitPane JTabbedPane JTable JTableHeader JTextArea JTextComponent JTextField\n"
                     +
                     "JTextPane JToggleButton JToolBar JToolTip JTree JViewport JWindow KerberosKey KerberosPrincipal\n"
                     +
                     "KerberosTicket Kernel Key KeyAdapter KeyAgreement KeyAgreementSpi KeyAlreadyExistsException\n"
                     +
                     "KeyboardFocusManager KeyEvent KeyEventDispatcher KeyEventPostProcessor KeyException KeyFactory\n"
                     +
                     "KeyFactorySpi KeyGenerator KeyGeneratorSpi KeyListener KeyManagementException KeyManager\n"
                     +
                     "KeyManagerFactory KeyManagerFactorySpi Keymap KeyPair KeyPairGenerator KeyPairGeneratorSpi KeyRep\n"
                     +
                     "KeySpec KeyStore KeyStoreBuilderParameters KeyStoreException KeyStoreSpi KeyStroke Label LabelUI\n"
                     +
                     "LabelView LanguageCallback LastOwnerException LayeredHighlighter LayoutFocusTraversalPolicy\n"
                     +
                     "LayoutManager LayoutManager2 LayoutQueue LDAPCertStoreParameters LdapContext LdapName\n"
                     +
                     "LdapReferralException Lease Level LimitExceededException Line Line2D LineBorder LineBreakMeasurer\n"
                     +
                     "LineEvent LineListener LineMetrics LineNumberInputStream LineNumberReader LineUnavailableException\n"
                     +
                     "LinkageError LinkedBlockingQueue LinkedHashMap LinkedHashSet LinkedList LinkException LinkLoopException\n"
                     +
                     "LinkRef List ListCellRenderer ListDataEvent ListDataListener ListenerNotFoundException ListIterator\n"
                     +
                     "ListModel ListResourceBundle ListSelectionEvent ListSelectionListener ListSelectionModel ListUI ListView\n"
                     +
                     "LoaderHandler Locale LocateRegistry Lock LockSupport Logger LoggingMXBean LoggingPermission LoginContext\n"
                     +
                     "LoginException LoginModule LogManager LogRecord LogStream Long LongBuffer LookAndFeel LookupOp\n"
                     +
                     "LookupTable Mac MacSpi MalformedInputException MalformedLinkException MalformedObjectNameException\n"
                     +
                     "MalformedParameterizedTypeException MalformedURLException ManagementFactory ManagementPermission\n"
                     +
                     "ManageReferralControl ManagerFactoryParameters Manifest Map MappedByteBuffer MarshalException\n"
                     +
                     "MarshalledObject MaskFormatter Matcher MatchResult Math MathContext MatteBorder MBeanAttributeInfo\n"
                     +
                     "MBeanConstructorInfo MBeanException MBeanFeatureInfo MBeanInfo MBeanNotificationInfo MBeanOperationInfo\n"
                     +
                     "MBeanParameterInfo MBeanPermission MBeanRegistration MBeanRegistrationException MBeanServer\n"
                     +
                     "MBeanServerBuilder MBeanServerConnection MBeanServerDelegate MBeanServerDelegateMBean MBeanServerFactory\n"
                     +
                     "MBeanServerForwarder MBeanServerInvocationHandler MBeanServerNotification MBeanServerNotificationFilter\n"
                     +
                     "MBeanServerPermission MBeanTrustPermission Media MediaName MediaPrintableArea MediaSize MediaSizeName\n"
                     +
                     "MediaTracker MediaTray Member MemoryCacheImageInputStream MemoryCacheImageOutputStream MemoryHandler\n"
                     +
                     "MemoryImageSource MemoryManagerMXBean MemoryMXBean MemoryNotificationInfo MemoryPoolMXBean MemoryType\n"
                     +
                     "MemoryUsage Menu MenuBar MenuBarUI MenuComponent MenuContainer MenuDragMouseEvent MenuDragMouseListener\n"
                     +
                     "MenuElement MenuEvent MenuItem MenuItemUI MenuKeyEvent MenuKeyListener MenuListener MenuSelectionManager\n"
                     +
                     "MenuShortcut MessageDigest MessageDigestSpi MessageFormat MetaEventListener MetalBorders MetalButtonUI\n"
                     +
                     "MetalCheckBoxIcon MetalCheckBoxUI MetalComboBoxButton MetalComboBoxEditor MetalComboBoxIcon\n"
                     +
                     "MetalComboBoxUI MetalDesktopIconUI MetalFileChooserUI MetalIconFactory MetalInternalFrameTitlePane\n"
                     +
                     "MetalInternalFrameUI MetalLabelUI MetalLookAndFeel MetalMenuBarUI MetalPopupMenuSeparatorUI\n"
                     +
                     "MetalProgressBarUI MetalRadioButtonUI MetalRootPaneUI MetalScrollBarUI MetalScrollButton\n"
                     +
                     "MetalScrollPaneUI MetalSeparatorUI MetalSliderUI MetalSplitPaneUI MetalTabbedPaneUI MetalTextFieldUI\n"
                     +
                     "MetalTheme MetalToggleButtonUI MetalToolBarUI MetalToolTipUI MetalTreeUI MetaMessage Method\n"
                     +
                     "MethodDescriptor MGF1ParameterSpec MidiChannel MidiDevice MidiDeviceProvider MidiEvent MidiFileFormat\n"
                     +
                     "MidiFileReader MidiFileWriter MidiMessage MidiSystem MidiUnavailableException MimeTypeParseException\n"
                     +
                     "MinimalHTMLWriter MissingFormatArgumentException MissingFormatWidthException MissingResourceException\n"
                     +
                     "Mixer MixerProvider MLet MLetMBean ModelMBean ModelMBeanAttributeInfo ModelMBeanConstructorInfo\n"
                     +
                     "ModelMBeanInfo ModelMBeanInfoSupport ModelMBeanNotificationBroadcaster ModelMBeanNotificationInfo\n"
                     +
                     "ModelMBeanOperationInfo ModificationItem Modifier Monitor MonitorMBean MonitorNotification\n"
                     +
                     "MonitorSettingException MouseAdapter MouseDragGestureRecognizer MouseEvent MouseInfo MouseInputAdapter\n"
                     +
                     "MouseInputListener MouseListener MouseMotionAdapter MouseMotionListener MouseWheelEvent\n"
                     +
                     "MouseWheelListener MultiButtonUI MulticastSocket MultiColorChooserUI MultiComboBoxUI MultiDesktopIconUI\n"
                     +
                     "MultiDesktopPaneUI MultiDoc MultiDocPrintJob MultiDocPrintService MultiFileChooserUI\n"
                     +
                     "MultiInternalFrameUI MultiLabelUI MultiListUI MultiLookAndFeel MultiMenuBarUI MultiMenuItemUI\n"
                     +
                     "MultiOptionPaneUI MultiPanelUI MultiPixelPackedSampleModel MultipleDocumentHandling MultipleMaster\n"
                     +
                     "MultiPopupMenuUI MultiProgressBarUI MultiRootPaneUI MultiScrollBarUI MultiScrollPaneUI MultiSeparatorUI\n"
                     +
                     "MultiSliderUI MultiSpinnerUI MultiSplitPaneUI MultiTabbedPaneUI MultiTableHeaderUI MultiTableUI\n"
                     +
                     "MultiTextUI MultiToolBarUI MultiToolTipUI MultiTreeUI MultiViewportUI MutableAttributeSet\n"
                     +
                     "MutableComboBoxModel MutableTreeNode Name NameAlreadyBoundException NameCallback NameClassPair\n"
                     +
                     "NameNotFoundException NameParser NamespaceChangeListener NamespaceContext Naming NamingEnumeration\n"
                     +
                     "NamingEvent NamingException NamingExceptionEvent NamingListener NamingManager NamingSecurityException\n"
                     +
                     "NavigationFilter NegativeArraySizeException NetPermission NetworkInterface NoClassDefFoundError\n"
                     +
                     "NoConnectionPendingException NodeChangeEvent NodeChangeListener NoInitialContextException\n"
                     +
                     "NoninvertibleTransformException NonReadableChannelException NonWritableChannelException\n"
                     +
                     "NoPermissionException NoRouteToHostException NoSuchAlgorithmException NoSuchAttributeException\n"
                     +
                     "NoSuchElementException NoSuchFieldError NoSuchFieldException NoSuchMethodError NoSuchMethodException\n"
                     +
                     "NoSuchObjectException NoSuchPaddingException NoSuchProviderException NotActiveException\n"
                     +
                     "NotBoundException NotCompliantMBeanException NotContextException Notification NotificationBroadcaster\n"
                     +
                     "NotificationBroadcasterSupport NotificationEmitter NotificationFilter NotificationFilterSupport\n"
                     +
                     "NotificationListener NotificationResult NotOwnerException NotSerializableException NotYetBoundException\n"
                     +
                     "NotYetConnectedException NullCipher NullPointerException Number NumberFormat NumberFormatException\n"
                     +
                     "NumberFormatter NumberOfDocuments NumberOfInterveningJobs NumberUp NumberUpSupported NumericShaper\n"
                     +
                     "OAEPParameterSpec Object ObjectChangeListener ObjectFactory ObjectFactoryBuilder ObjectInput\n"
                     +
                     "ObjectInputStream ObjectInputValidation ObjectInstance ObjectName ObjectOutput ObjectOutputStream\n"
                     +
                     "ObjectStreamClass ObjectStreamConstants ObjectStreamException ObjectStreamField ObjectView ObjID\n"
                     +
                     "Observable Observer OceanTheme OpenDataException OpenMBeanAttributeInfo OpenMBeanAttributeInfoSupport\n"
                     +
                     "OpenMBeanConstructorInfo OpenMBeanConstructorInfoSupport OpenMBeanInfo OpenMBeanInfoSupport\n"
                     +
                     "OpenMBeanOperationInfo OpenMBeanOperationInfoSupport OpenMBeanParameterInfo\n"
                     +
                     "OpenMBeanParameterInfoSupport OpenType OperatingSystemMXBean Operation OperationNotSupportedException\n"
                     +
                     "OperationsException Option OptionalDataException OptionPaneUI OrientationRequested OutOfMemoryError\n"
                     +
                     "OutputDeviceAssigned OutputKeys OutputStream OutputStreamWriter OverlappingFileLockException\n"
                     +
                     "OverlayLayout Override Owner Pack200 Package PackedColorModel Pageable PageAttributes\n"
                     +
                     "PagedResultsControl PagedResultsResponseControl PageFormat PageRanges PagesPerMinute PagesPerMinuteColor\n"
                     +
                     "Paint PaintContext PaintEvent Panel PanelUI Paper ParagraphView ParameterBlock ParameterDescriptor\n"
                     +
                     "ParameterizedType ParameterMetaData ParseException ParsePosition Parser ParserConfigurationException\n"
                     +
                     "ParserDelegator PartialResultException PasswordAuthentication PasswordCallback PasswordView Patch\n"
                     +
                     "PathIterator Pattern PatternSyntaxException PBEKey PBEKeySpec PBEParameterSpec PDLOverrideSupported\n"
                     +
                     "Permission PermissionCollection Permissions PersistenceDelegate PersistentMBean PhantomReference Pipe\n"
                     +
                     "PipedInputStream PipedOutputStream PipedReader PipedWriter PixelGrabber PixelInterleavedSampleModel\n"
                     +
                     "PKCS8EncodedKeySpec PKIXBuilderParameters PKIXCertPathBuilderResult PKIXCertPathChecker\n"
                     +
                     "PKIXCertPathValidatorResult PKIXParameters PlainDocument PlainView Point Point2D PointerInfo Policy\n"
                     +
                     "PolicyNode PolicyQualifierInfo Polygon PooledConnection Popup PopupFactory PopupMenu PopupMenuEvent\n"
                     +
                     "PopupMenuListener PopupMenuUI Port PortableRemoteObject PortableRemoteObjectDelegate\n"
                     +
                     "PortUnreachableException Position Predicate PreferenceChangeEvent PreferenceChangeListener Preferences\n"
                     +
                     "PreferencesFactory PreparedStatement PresentationDirection Principal Printable PrinterAbortException\n"
                     +
                     "PrinterException PrinterGraphics PrinterInfo PrinterIOException PrinterIsAcceptingJobs PrinterJob\n"
                     +
                     "PrinterLocation PrinterMakeAndModel PrinterMessageFromOperator PrinterMoreInfo\n"
                     +
                     "PrinterMoreInfoManufacturer PrinterName PrinterResolution PrinterState PrinterStateReason\n"
                     +
                     "PrinterStateReasons PrinterURI PrintEvent PrintException PrintGraphics PrintJob PrintJobAdapter\n"
                     +
                     "PrintJobAttribute PrintJobAttributeEvent PrintJobAttributeListener PrintJobAttributeSet PrintJobEvent\n"
                     +
                     "PrintJobListener PrintQuality PrintRequestAttribute PrintRequestAttributeSet PrintService\n"
                     +
                     "PrintServiceAttribute PrintServiceAttributeEvent PrintServiceAttributeListener PrintServiceAttributeSet\n"
                     +
                     "PrintServiceLookup PrintStream PrintWriter PriorityBlockingQueue PriorityQueue PrivateClassLoader\n"
                     +
                     "PrivateCredentialPermission PrivateKey PrivateMLet PrivilegedAction PrivilegedActionException\n"
                     +
                     "PrivilegedExceptionAction Process ProcessBuilder ProfileDataException ProgressBarUI ProgressMonitor\n"
                     +
                     "ProgressMonitorInputStream Properties PropertyChangeEvent PropertyChangeListener\n"
                     +
                     "PropertyChangeListenerProxy PropertyChangeSupport PropertyDescriptor PropertyEditor\n"
                     +
                     "PropertyEditorManager PropertyEditorSupport PropertyPermission PropertyResourceBundle\n"
                     +
                     "PropertyVetoException ProtectionDomain ProtocolException Provider ProviderException Proxy ProxySelector\n"
                     +
                     "PSource PSSParameterSpec PublicKey PushbackInputStream PushbackReader QName QuadCurve2D Query QueryEval\n"
                     +
                     "QueryExp Queue QueuedJobCount Random RandomAccess RandomAccessFile Raster RasterFormatException RasterOp\n"
                     +
                     "RC2ParameterSpec RC5ParameterSpec Rdn Readable ReadableByteChannel Reader ReadOnlyBufferException\n"
                     +
                     "ReadWriteLock RealmCallback RealmChoiceCallback Receiver Rectangle Rectangle2D RectangularShape\n"
                     +
                     "ReentrantLock ReentrantReadWriteLock Ref RefAddr Reference Referenceable ReferenceQueue\n"
                     +
                     "ReferenceUriSchemesSupported ReferralException ReflectionException ReflectPermission Refreshable\n"
                     +
                     "RefreshFailedException Region RegisterableService Registry RegistryHandler RejectedExecutionException\n"
                     +
                     "RejectedExecutionHandler Relation RelationException RelationNotFoundException RelationNotification\n"
                     +
                     "RelationService RelationServiceMBean RelationServiceNotRegisteredException RelationSupport\n"
                     +
                     "RelationSupportMBean RelationType RelationTypeNotFoundException RelationTypeSupport Remote RemoteCall\n"
                     +
                     "RemoteException RemoteObject RemoteObjectInvocationHandler RemoteRef RemoteServer RemoteStub\n"
                     +
                     "RenderableImage RenderableImageOp RenderableImageProducer RenderContext RenderedImage\n"
                     +
                     "RenderedImageFactory Renderer RenderingHints RepaintManager ReplicateScaleFilter RequestingUserName\n"
                     +
                     "RequiredModelMBean RescaleOp ResolutionSyntax Resolver ResolveResult ResourceBundle ResponseCache Result\n"
                     +
                     "ResultSet ResultSetMetaData Retention RetentionPolicy ReverbType RGBImageFilter RMIClassLoader\n"
                     +
                     "RMIClassLoaderSpi RMIClientSocketFactory RMIConnection RMIConnectionImpl RMIConnectionImpl_Stub\n"
                     +
                     "RMIConnector RMIConnectorServer RMIFailureHandler RMIIIOPServerImpl RMIJRMPServerImpl\n"
                     +
                     "RMISecurityException RMISecurityManager RMIServer RMIServerImpl RMIServerImpl_Stub\n"
                     +
                     "RMIServerSocketFactory RMISocketFactory Robot Role RoleInfo RoleInfoNotFoundException RoleList\n"
                     +
                     "RoleNotFoundException RoleResult RoleStatus RoleUnresolved RoleUnresolvedList RootPaneContainer\n"
                     +
                     "RootPaneUI RoundingMode RoundRectangle2D RowMapper RowSet RowSetEvent RowSetInternal RowSetListener\n"
                     +
                     "RowSetMetaData RowSetMetaDataImpl RowSetReader RowSetWarning RowSetWriter RSAKey RSAKeyGenParameterSpec\n"
                     +
                     "RSAMultiPrimePrivateCrtKey RSAMultiPrimePrivateCrtKeySpec RSAOtherPrimeInfo RSAPrivateCrtKey\n"
                     +
                     "RSAPrivateCrtKeySpec RSAPrivateKey RSAPrivateKeySpec RSAPublicKey RSAPublicKeySpec RTFEditorKit\n"
                     +
                     "RuleBasedCollator Runnable Runtime RuntimeErrorException RuntimeException RuntimeMBeanException\n"
                     +
                     "RuntimeMXBean RuntimeOperationsException RuntimePermission SampleModel Sasl SaslClient SaslClientFactory\n"
                     +
                     "SaslException SaslServer SaslServerFactory Savepoint SAXParser SAXParserFactory SAXResult SAXSource\n"
                     +
                     "SAXTransformerFactory Scanner ScatteringByteChannel ScheduledExecutorService ScheduledFuture\n"
                     +
                     "ScheduledThreadPoolExecutor Schema SchemaFactory SchemaFactoryLoader SchemaViolationException Scrollable\n"
                     +
                     "Scrollbar ScrollBarUI ScrollPane ScrollPaneAdjustable ScrollPaneConstants ScrollPaneLayout ScrollPaneUI\n"
                     +
                     "SealedObject SearchControls SearchResult SecretKey SecretKeyFactory SecretKeyFactorySpi SecretKeySpec\n"
                     +
                     "SecureCacheResponse SecureClassLoader SecureRandom SecureRandomSpi Security SecurityException\n"
                     +
                     "SecurityManager SecurityPermission Segment SelectableChannel SelectionKey Selector SelectorProvider\n"
                     +
                     "Semaphore SeparatorUI Sequence SequenceInputStream Sequencer SerialArray SerialBlob SerialClob\n"
                     +
                     "SerialDatalink SerialException Serializable SerializablePermission SerialJavaObject SerialRef\n"
                     +
                     "SerialStruct ServerCloneException ServerError ServerException ServerNotActiveException ServerRef\n"
                     +
                     "ServerRuntimeException ServerSocket ServerSocketChannel ServerSocketFactory ServiceNotFoundException\n"
                     +
                     "ServicePermission ServiceRegistry ServiceUI ServiceUIFactory ServiceUnavailableException Set\n"
                     +
                     "SetOfIntegerSyntax Severity Shape ShapeGraphicAttribute SheetCollate Short ShortBuffer\n"
                     +
                     "ShortBufferException ShortLookupTable ShortMessage Sides Signature SignatureException SignatureSpi\n"
                     +
                     "SignedObject Signer SimpleAttributeSet SimpleBeanInfo SimpleDateFormat SimpleDoc SimpleFormatter\n"
                     +
                     "SimpleTimeZone SimpleType SinglePixelPackedSampleModel SingleSelectionModel Size2DSyntax\n"
                     +
                     "SizeLimitExceededException SizeRequirements SizeSequence Skeleton SkeletonMismatchException\n"
                     +
                     "SkeletonNotFoundException SliderUI Socket SocketAddress SocketChannel SocketException SocketFactory\n"
                     +
                     "SocketHandler SocketImpl SocketImplFactory SocketOptions SocketPermission SocketSecurityException\n"
                     +
                     "SocketTimeoutException SoftBevelBorder SoftReference SortControl SortedMap SortedSet\n"
                     +
                     "SortingFocusTraversalPolicy SortKey SortResponseControl Soundbank SoundbankReader SoundbankResource\n"
                     +
                     "Source SourceDataLine SourceLocator SpinnerDateModel SpinnerListModel SpinnerModel SpinnerNumberModel\n"
                     +
                     "SpinnerUI SplitPaneUI Spring SpringLayout SQLData SQLException SQLInput SQLInputImpl SQLOutput\n"
                     +
                     "SQLOutputImpl SQLPermission SQLWarning SSLContext SSLContextSpi SSLEngine SSLEngineResult SSLException\n"
                     +
                     "SSLHandshakeException SSLKeyException SSLPeerUnverifiedException SSLPermission SSLProtocolException\n"
                     +
                     "SslRMIClientSocketFactory SslRMIServerSocketFactory SSLServerSocket SSLServerSocketFactory SSLSession\n"
                     +
                     "SSLSessionBindingEvent SSLSessionBindingListener SSLSessionContext SSLSocket SSLSocketFactory Stack\n"
                     +
                     "StackOverflowError StackTraceElement StandardMBean StartTlsRequest StartTlsResponse StateEdit\n"
                     +
                     "StateEditable StateFactory Statement StreamCorruptedException StreamHandler StreamPrintService\n"
                     +
                     "StreamPrintServiceFactory StreamResult StreamSource StreamTokenizer StrictMath String StringBuffer\n"
                     +
                     "StringBufferInputStream StringBuilder StringCharacterIterator StringContent\n"
                     +
                     "StringIndexOutOfBoundsException StringMonitor StringMonitorMBean StringReader StringRefAddr\n"
                     +
                     "StringSelection StringTokenizer StringValueExp StringWriter Stroke Struct Stub StubDelegate\n"
                     +
                     "StubNotFoundException Style StyleConstants StyleContext StyledDocument StyledEditorKit StyleSheet\n"
                     +
                     "Subject SubjectDelegationPermission SubjectDomainCombiner SupportedValuesAttribute SuppressWarnings\n"
                     +
                     "SwingConstants SwingPropertyChangeSupport SwingUtilities SyncFactory SyncFactoryException\n"
                     +
                     "SyncFailedException SynchronousQueue SyncProvider SyncProviderException SyncResolver SynthConstants\n"
                     +
                     "SynthContext Synthesizer SynthGraphicsUtils SynthLookAndFeel SynthPainter SynthStyle SynthStyleFactory\n"
                     +
                     "SysexMessage System SystemColor SystemFlavorMap TabableView TabbedPaneUI TabExpander TableCellEditor\n"
                     +
                     "TableCellRenderer TableColumn TableColumnModel TableColumnModelEvent TableColumnModelListener\n"
                     +
                     "TableHeaderUI TableModel TableModelEvent TableModelListener TableUI TableView TabSet TabStop TabularData\n"
                     +
                     "TabularDataSupport TabularType TagElement Target TargetDataLine TargetedNotification Templates\n"
                     +
                     "TemplatesHandler TextAction TextArea TextAttribute TextComponent TextEvent TextField TextHitInfo\n"
                     +
                     "TextInputCallback TextLayout TextListener TextMeasurer TextOutputCallback TextSyntax TextUI TexturePaint\n"
                     +
                     "Thread ThreadDeath ThreadFactory ThreadGroup ThreadInfo ThreadLocal ThreadMXBean ThreadPoolExecutor\n"
                     +
                     "Throwable Tie TileObserver Time TimeLimitExceededException TimeoutException Timer\n"
                     +
                     "TimerAlarmClockNotification TimerMBean TimerNotification TimerTask Timestamp TimeUnit TimeZone\n"
                     +
                     "TitledBorder ToolBarUI Toolkit ToolTipManager ToolTipUI TooManyListenersException Track\n"
                     +
                     "TransactionalWriter TransactionRequiredException TransactionRolledbackException Transferable\n"
                     +
                     "TransferHandler TransformAttribute Transformer TransformerConfigurationException TransformerException\n"
                     +
                     "TransformerFactory TransformerFactoryConfigurationError TransformerHandler Transmitter Transparency\n"
                     +
                     "TreeCellEditor TreeCellRenderer TreeExpansionEvent TreeExpansionListener TreeMap TreeModel\n"
                     +
                     "TreeModelEvent TreeModelListener TreeNode TreePath TreeSelectionEvent TreeSelectionListener\n"
                     +
                     "TreeSelectionModel TreeSet TreeUI TreeWillExpandListener TrustAnchor TrustManager TrustManagerFactory\n"
                     +
                     "TrustManagerFactorySpi Type TypeInfoProvider TypeNotPresentException Types TypeVariable UID UIDefaults\n"
                     +
                     "UIManager UIResource UndeclaredThrowableException UndoableEdit UndoableEditEvent UndoableEditListener\n"
                     +
                     "UndoableEditSupport UndoManager UnexpectedException UnicastRemoteObject UnknownError\n"
                     +
                     "UnknownFormatConversionException UnknownFormatFlagsException UnknownGroupException UnknownHostException\n"
                     +
                     "UnknownObjectException UnknownServiceException UnmappableCharacterException UnmarshalException\n"
                     +
                     "UnmodifiableClassException UnmodifiableSetException UnrecoverableEntryException\n"
                     +
                     "UnrecoverableKeyException Unreferenced UnresolvedAddressException UnresolvedPermission\n"
                     +
                     "UnsatisfiedLinkError UnsolicitedNotification UnsolicitedNotificationEvent\n"
                     +
                     "UnsolicitedNotificationListener UnsupportedAddressTypeException UnsupportedAudioFileException\n"
                     +
                     "UnsupportedCallbackException UnsupportedCharsetException UnsupportedClassVersionError\n"
                     +
                     "UnsupportedEncodingException UnsupportedFlavorException UnsupportedLookAndFeelException\n"
                     +
                     "UnsupportedOperationException URI URIException URIResolver URISyntax URISyntaxException URL\n"
                     +
                     "URLClassLoader URLConnection URLDecoder URLEncoder URLStreamHandler URLStreamHandlerFactory\n"
                     +
                     "UTFDataFormatException Util UtilDelegate Utilities UUID Validator ValidatorHandler ValueExp ValueHandler\n"
                     +
                     "ValueHandlerMultiFormat VariableHeightLayoutCache Vector VerifyError VetoableChangeListener\n"
                     +
                     "VetoableChangeListenerProxy VetoableChangeSupport View ViewFactory ViewportLayout ViewportUI\n"
                     +
                     "VirtualMachineError Visibility VMID VoiceStatus Void VolatileImage WeakHashMap WeakReference WebRowSet\n"
                     +
                     "WildcardType Window WindowAdapter WindowConstants WindowEvent WindowFocusListener WindowListener\n"
                     +
                     "WindowStateListener WrappedPlainView WritableByteChannel WritableRaster WritableRenderedImage\n"
                     +
                     "WriteAbortedException Writer X500Principal X500PrivateCredential X509Certificate X509CertSelector\n"
                     +
                     "X509CRL X509CRLEntry X509CRLSelector X509EncodedKeySpec X509ExtendedKeyManager X509Extension\n"
                     +
                     "X509KeyManager X509TrustManager XAConnection XADataSource XAException XAResource Xid XMLConstants\n"
                     +
                     "XMLDecoder XMLEncoder XMLFormatter XMLGregorianCalendar XMLParseException XmlReader XmlWriter XPath\n"
                     +
                     "XPathConstants XPathException XPathExpression XPathExpressionException XPathFactory\n" +
                     "XPathFactoryConfigurationException XPathFunction XPathFunctionException XPathFunctionResolver\n" +
                     "XPathVariableResolver ZipEntry ZipException ZipFile ZipInputStream ZipOutputStream ZoneView";

   public static final String[] PREDEFINED_TYPES = cleanUp();
   public static final String[] EXCEPTION_TYPES = extractExceptions(PREDEFINED_TYPES);

   private static String[] cleanUp()
   {
      return raw_string.replaceAll("\n", " ").split(" ");
   }

   private static String[] extractExceptions(String[] types)
   {

      List<String> match = new ArrayList<String>();
      for (String s : types)
      {
         if (s.endsWith("Error") || s.endsWith("Exception"))
         {
            match.add(s);
         }
      }
      return match.toArray(new String[] {});
   }
}