/**
 * 
 */
package eu.planets_project.tb.gui.backing;

import java.util.List;

import javax.faces.model.DataModel;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class PagedListDataModel extends DataModel {

    private int rowIndex = -1;  
    private int totalNumRows;  
    private int pageSize;  
    private List<?> list;  
    
    public PagedListDataModel(List<?> list, int totalNumRows, int pageSize)
      {
        setWrappedData(list);
        this.totalNumRows = totalNumRows;
        this.pageSize = pageSize;
      }

    /* (non-Javadoc)
     * @see javax.faces.model.DataModel#isRowAvailable()
     */
    @Override
      public boolean isRowAvailable()
      {
        if(list == null)
          return false;
        int rowIndex = getRowIndex();
        if(rowIndex >=0 && rowIndex < list.size())
          return true;
        else
          return false;
      }

      /* (non-Javadoc)
       * @see javax.faces.model.DataModel#getRowCount()
       */
      @Override
      public int getRowCount()
      {
        return totalNumRows;
      }

      /* (non-Javadoc)
       * @see javax.faces.model.DataModel#getRowData()
       */
      @Override
      public Object getRowData()
      {
        if(list == null)
          return null;
        else if(!isRowAvailable())
          throw new IllegalArgumentException();
        else
        {
          int dataIndex = getRowIndex();
          return list.get(dataIndex);
        }
      }

      /* (non-Javadoc)
       * @see javax.faces.model.DataModel#getRowIndex()
       */
      @Override
      public int getRowIndex()
      {
        return (rowIndex % pageSize);
      }

      /* (non-Javadoc)
       * @see javax.faces.model.DataModel#setRowIndex(int)
       */
      @Override
      public void setRowIndex(int rowIndex)
      {
        this.rowIndex = rowIndex;
      }

      /* (non-Javadoc)
       * @see javax.faces.model.DataModel#getWrappedData()
       */
      @Override
      public Object getWrappedData()
      {
        return list;
      }

      /* (non-Javadoc)
       * @see javax.faces.model.DataModel#setWrappedData(java.lang.Object)
       */
      @Override
      public void setWrappedData(Object list)
      {
        this.list = (List) list;
      }

}
