package net.bioclipse.plugins.views;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.model.CDKChemObject;
import net.bioclipse.model.CDKPDBStructureObject;
import net.bioclipse.model.CDKResource;
import net.bioclipse.util.ImageUtils;
import net.bioclipse.views.BioResourceView;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStrand;
import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;



public class ChemTreeView extends ViewPart implements ISelectionListener, ISelectionChangedListener {

    private static final Logger logger = net.bioclipse.Bc_cdkPlugin.getLogManager().getLogger(ChemObjectTreeView.class.toString());
    public static final String ID = "net.bioclipse.plugins.views.ChemObjectTreeView";
    private TreeViewer tree;

    private final static String CDK_PREFIX = "org.openscience.cdk.";
    private final static String PDB_PREFIX = "protein.data.PDB";


    // cached images
    private final static Image carbonImage = ImageUtils.getImageDescriptor("atom_c").createImage();
    private final static Image hydrogenImage = ImageUtils.getImageDescriptor("atom_h").createImage();
    private final static Image nitrogenImage = ImageUtils.getImageDescriptor("atom_n").createImage();
    private final static Image oxygenImage = ImageUtils.getImageDescriptor("atom_o").createImage();

    private CDKResource savedResource;

    public void createPartControl(Composite parent) {

        logger.debug("Initiating the ChemTree...");

        tree = new TreeViewer(parent);
        tree.setContentProvider(new ChemObjectContentProvider());
        tree.setLabelProvider(new ChemObjectLabelProvider());

        //Register this page as a listener for selections
        //We want to update information based on selection i e g TreeViewer
        getViewSite().getPage().addSelectionListener(this);

        //Register the treeViewer so that others may react upon it
        getSite().setSelectionProvider(tree);

        //Get selection from BioresView if exists and set as input
        updateTree(BioResourceView.getSelectedResource());

    }

    public void setFocus() {
        this.tree.getControl().setFocus();

    }
    @SuppressWarnings("unchecked")
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if(part==this)
            return;
        if (!(selection instanceof IStructuredSelection)) return;

        Iterator it=((IStructuredSelection)selection).iterator();

        if (it.hasNext()) {

            while( it.hasNext()){
                Object obj=it.next();
                if(obj instanceof CDKResource){
                    Object first = ((IStructuredSelection)selection).getFirstElement();
                    updateTree(first);
                }else{
                    //FIXME not working - the selection is properly set, but highlighting does not work
                    tree.setSelection(selection, true);
                }
                break;
            }
        }
    }


    private void updateTree(Object first) {

        if (first==null) return;

        if (!(first instanceof CDKResource)) return;

        CDKResource cdkres= (CDKResource)first;
        if (!(cdkres.isParsed())) return;

        //Do not re-read same object
        if (cdkres.equals(savedResource)) return;
        savedResource=cdkres;

        Object obj=cdkres.getParsedResource();
        if (obj==null) return;

        boolean isBioPol=false;
        if (obj instanceof IChemFile) {
            IChemFile chemfile = (IChemFile) obj;
            List atomContainerList = getIAtomContainerFromChemFile(chemfile);

            ArrayList objects = new ArrayList();
            Iterator iterator = atomContainerList.iterator();
            while(iterator.hasNext()){
                IAtomContainer ac = (IAtomContainer)iterator.next();
                if (ac instanceof IBioPolymer) {
                    objects.add(ac);
                    logger.debug("Found a BioPolymer");
                    isBioPol=true;
                }
            }
        }

        //Decide which content and label provider to use
        if (isBioPol){
            tree.setContentProvider(new BioPolymerContentProvider());
            tree.setLabelProvider(new BioPolymerLabelProvider());
            logger.debug("Set BioPolymerProviders for ChemTree");
        }
        else{
            tree.setContentProvider(new ChemObjectContentProvider());
            tree.setLabelProvider(new ChemObjectLabelProvider());
            logger.debug("Set ChemObjectProviders for ChemTree");
        }

        tree.setInput(first);
        tree.expandAll();

    }

    private List getIAtomContainerFromChemFile(IChemFile parsedResource) {
        List acList = ChemFileManipulator.getAllAtomContainers(parsedResource);
        return acList;
    }



    class ChemObjectLabelProvider implements ILabelProvider {

        public Image getImage(Object element) {

            if (element instanceof CDKChemObject) {
                CDKChemObject obj=(CDKChemObject)element;

                IChemObject chemobj=obj.getChemobj();

                if (chemobj instanceof IAtom) {
                    IAtom atom = (IAtom) chemobj;
                    if (atom.getSymbol().compareTo("C")==0){
                        return carbonImage;
                    }
                    else if (atom.getSymbol().compareTo("H")==0){
                        return hydrogenImage;
                    }
                    else if (atom.getSymbol().compareTo("N")==0){
                        return nitrogenImage;
                    }
                    else if (atom.getSymbol().compareTo("O")==0){
                        return oxygenImage;
                    }
                }
                if (chemobj instanceof IBond) {
                    //TODO: add image for bonds
                    return null;
                }

            }

            //No match, no image
            return null;
        }

        public String getText(Object element) {
            if (element instanceof CDKChemObject) {
                CDKChemObject obj=(CDKChemObject)element;

                IChemObject chemobj=obj.getChemobj();
                return toString(chemobj);
            } else if (element instanceof IChemObject) {
                return toString((IChemObject)element);
            }
            return "N/A";
        }

        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub
        }

        public void dispose() {
            // TODO Auto-generated method stub
        }

        public boolean isLabelProperty(Object element, String property) {
            return (getText(element).equals(property));
        }

        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub
        }

        public String toString(IChemObject object) {
            StringBuffer name = new StringBuffer();

            // remove the "org.openscience.cdk." prefix
            name.append(removePrefixes(object.getClass().getName()));
            if (object instanceof IAtom) {
                name.append(" " + ((IAtom)object).getSymbol());
            } else if (object instanceof IBond) {
                name.append(" " + ((IBond)object).getOrder());
            }
            return name.toString();
        }
    }

    class ChemObjectContentProvider implements ITreeContentProvider {

        ChemObjectContentProvider() {}

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IChemObject) {
                return getChemObjectChildren((IChemObject)parentElement);
            } else if (parentElement instanceof CDKChemObject) {
                return getChildren(((CDKChemObject)parentElement).getChemobj());
            }

            return new IChemObject[0];
        }

        public Object getParent(Object element) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean hasChildren(Object element) {
            return (getChildren(element).length > 0);
        }

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof CDKResource && ((CDKResource)inputElement).getParsedResource() != null) {
                return new Object[]{((CDKResource)inputElement).getParsedResource()};
            }
            return new IChemObject[0];
        }

        public void dispose() {
            // TODO Auto-generated method stub

        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }
    }

    class BioPolymerContentProvider implements ITreeContentProvider {

        BioPolymerContentProvider() {}

        public Object[] getChildren(Object parentElement) {
//            logger.debug("Finding children of: " + parentElement.getClass().getName());

            // optionall unwrap IChemObject
            if (parentElement instanceof CDKChemObject) {
                parentElement = ((CDKChemObject)parentElement).getChemobj();
            }

            if (parentElement instanceof IBioPolymer) {
                IBioPolymer polymer = (IBioPolymer)parentElement;
                // childs of BioPolymer are it's Strands
//                logger.debug("Strand count: " + polymer.getStrandCount());
                int strandCount = polymer.getStrandCount();
                int structCount = 0;
                if (polymer instanceof PDBPolymer)
                    structCount = structCount + ((PDBPolymer)polymer).getStructures().size();
                Object[] strands = new Object[strandCount+structCount];
                Iterator names = polymer.getStrandNames().iterator();
                for (int i=0; i<strandCount; i++) {
                    IStrand str=polymer.getStrand((String)names.next());
                    logger.debug("Added strand: " + str.getStrandName());
                    strands[i] = new CDKChemObject(str);
                }
                if (polymer instanceof PDBPolymer) {
                    Iterator structs = ((PDBPolymer)polymer).getStructures().iterator();
                    int i = 0;
                    while (structs.hasNext()) {
                        strands[strandCount+i] = new CDKPDBStructureObject((PDBStructure)structs.next());
                        i++;
                    }
                }
                return strands;
            } else if (parentElement instanceof IStrand) {
                IStrand strand = (IStrand)parentElement;
                // childs of BioPolymer are it's Strands
//                logger.debug("Monomer count: " + strand.getMonomerCount());
                Object[] monomers = new Object[strand.getMonomerCount()];
                Iterator names;
                if (parentElement instanceof PDBStrand) {
                    names = ((PDBStrand)parentElement).getMonomerNamesInSequentialOrder().iterator();
                } else {
                    names = strand.getMonomerNames().iterator();
                }
                for (int i=0; i<monomers.length; i++) {
                    monomers[i] = new CDKChemObject(strand.getMonomer((String)names.next()));
                }
                return monomers;
            }

            return new IChemObject[0];
        }

        public Object getParent(Object element) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean hasChildren(Object element) {
            return (getChildren(element).length > 0);
        }

        public Object[] getElements(Object inputElement) {
//            logger.debug("Finding elements of: " + inputElement.getClass().getName());
            if (inputElement instanceof CDKResource) {
                CDKResource cdkResource = (CDKResource) inputElement;
                if (cdkResource != null && cdkResource.getParsedResource() != null) {
                    List atomContainerList = (getIAtomContainerFromChemFile((IChemFile) cdkResource.getParsedResource()));
                    Iterator iterator = atomContainerList.iterator();

//                    IAtomContainer[] atomContainer = (getIAtomContainerFromChemFile((ChemFile) cdkResource.getParsedResource()));

                    ArrayList objects = new ArrayList();
                    while(iterator.hasNext()){
                        IAtomContainer ac = (IAtomContainer)iterator.next();
                        if (ac instanceof IBioPolymer) {
                            objects.add(ac);
                        }
                    }
                    return objects.toArray();
                }
            }
            return new IChemObject[0];
        }

        public void dispose() {
            // TODO Auto-generated method stub

        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
        }
    }

    class BioPolymerLabelProvider implements ILabelProvider {

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            if (element instanceof CDKChemObject) {
                CDKChemObject obj=(CDKChemObject)element;

                IChemObject chemobj=obj.getChemobj();
                return toString(chemobj);
            } else if (element instanceof CDKPDBStructureObject) {
                CDKPDBStructureObject obj = (CDKPDBStructureObject)element;

                PDBStructure structure = obj.getChemobj();
                return " " + structure.getStructureType() +
                " " + structure.getStartSequenceNumber() +
                "-" + structure.getEndSequenceNumber();
            } else if (element instanceof IChemObject) {
                return toString((IChemObject)element);
            }
            return "N/A";
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return (getText(element).equals(property));
        }

        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub
        }

        public String toString(IChemObject object) {
            StringBuffer name = new StringBuffer();

            // remove the "org.openscience.cdk." prefix
            name.append(removePrefixes(object.getClass().getName()));
            if (object instanceof IBioPolymer) {
                if (((IBioPolymer)object).getID() != null) {
                    name.append(" " + ((IBioPolymer)object).getID());
                } else if (((IChemObject)object).getProperty(CDKConstants.TITLE) != null) {
                    name.append(" " + ((IBioPolymer)object).getProperty(CDKConstants.TITLE));
                }
            } else if (object instanceof IStrand) {
                name.append(" " + ((IStrand)object).getStrandName());
            } else if (object instanceof IMonomer) {
                name.append(" " + ((IMonomer)object).getMonomerName());
            }
            return name.toString();
        }
    }



    @SuppressWarnings("unchecked")
    private Object[] getChemObjectChildren(IChemObject object) {

        Class reflectedClass = object.getClass();
//        logger.debug("getTree for class: " + reflectedClass);
        // get all fields in this ChemObject
        Field[] fields = getFields(reflectedClass);
//        logger.debug(reflectedClass.getName() + " #fields: " + fields.length);

        List children = new ArrayList();
        for (int i=0; i<fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
//            logger.debug("Field name: " + f.getName());
//            logger.debug("Field type: " + f.getType().getName());
            try {
                // get an instance of the object in the field
                Object fieldObject = f.get(object);
                if (fieldObject != null) {
//                    logger.debug("Field value: " + fieldObject.getClass().getName());
                    if (fieldObject instanceof IChemObject) {
                        // yes, found a ChemObject!
//                        logger.debug("Recursing into this object");

                        CDKChemObject co=new CDKChemObject((IChemObject)fieldObject);

//                        children.add(fieldObject);
                        children.add(co);
                    } else if (fieldObject instanceof IChemObject[]) {
                        // yes, found a Array!
//                        logger.debug("Recursing into this Array");
                        // determine what kind of Array
                        IChemObject[] objects = (IChemObject[])fieldObject;
                        int count = objects.length;
                        if (count>0){
                        // Because the count above gives the array length and not the number
                        // of not null objects the array, some intelligence must be added
                        if (object instanceof IAtomContainer && objects[0] != null) {
//                            logger.debug("field class: " + objects[0].getClass().getName());
                            if (objects[0] instanceof IAtom) {
                                count = ((IAtomContainer)object).getAtomCount();
                            } else if (objects[0] instanceof IElectronContainer) {
                                count = ((IAtomContainer)object).getElectronContainerCount();
                            } else {
//                                logger.debug("Object not counted!");
                            }
                        } else if (object instanceof IChemSequence && objects[0] != null) {
                            if (objects[0] instanceof IChemModel) {
                                count = ((IChemSequence)object).getChemModelCount();
                            } else {
//                                logger.debug("Object not counted!");
                            }
                        } else if (object instanceof IChemFile && objects[0] != null) {
                            if (objects[0] instanceof IChemSequence) {
                                count = ((IChemFile)object).getChemSequenceCount();
                            } else {
                                logger.debug("Object not counted!");
                            }
                        } else {
//                            logger.debug("Not going to recurse into arrays that are not field of AtomContainer");
                        }
//                        logger.debug("Found #entries in array: " + count);
                        // now start actual looping over child objects
                        for (int j=0; j<count; j++) {
                            if (objects[j] != null) {

                                CDKChemObject co=new CDKChemObject((IChemObject)objects[j]);
                                children.add(co);

//                                children.add(objects[j]);
                            }
                        }
                        }
                    }
                } else {
//                    logger.debug("Field value: null");
                }
            } catch (Exception e) {
                logger.debug("Error while constructing COT: " + e.getMessage());
                logger.debug(e);
                e.printStackTrace();
            }
        }
        return (children.toArray());
    }

    private Field[] getFields(Class reflectedClass) {
        Field[] fields = new Field[0];

        try {
            if (reflectedClass.newInstance() instanceof IChemObject) {
                Field[] ownFields = reflectedClass.getDeclaredFields();

                // try its super class too, as long as it is still a ChemObject...
                Class superClass = reflectedClass.getSuperclass();
                Field[] superFields = getFields(superClass);

                if (superFields.length > 0) {
                    // merge them
                    fields = new Field[ownFields.length + superFields.length];
                    System.arraycopy(ownFields, 0, fields, 0, ownFields.length);
                    System.arraycopy(superFields, 0, fields, ownFields.length, superFields.length);
                } else {
                    fields = ownFields;
                }
            }
        } catch (IllegalAccessException event) {
            logger.error("IllegalAccess: " + event.getMessage());
        } catch (InstantiationException event) {
//            logger.error("InstantiationException: " + event.getMessage());
        };
        return fields;
    }

    private String removePrefixes(String className) {
        className = className.substring(CDK_PREFIX.length());
        if (className.startsWith(PDB_PREFIX)) {
            className = className.substring(PDB_PREFIX.length());
        }
        return className;

    }

    @Override
    public void dispose() {
        IViewPart bioResView = this.getViewSite().getPage().findView("net.bioclipse.views.BioResourceView");
        if (bioResView != null){
            if (bioResView instanceof BioResourceView) {
                ((BioResourceView)bioResView).removeSelectionChangedListener(this);
            }
        }
        getViewSite().getPage().removeSelectionListener(this);
        super.dispose();

    }

    public void selectionChanged(SelectionChangedEvent event) {
    }

}
